package bayern.kickner.kotlin_extensions_android

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.provider.CalendarContract.Reminders
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotnexlib.ResultOf
import java.util.TimeZone
import kotlin.time.Duration.Companion.minutes


/**
 * Provides helper functions to interact with the Android Calendar Provider.
 * Requires necessary `READ_CALENDAR` and/or `WRITE_CALENDAR` permissions.
 *
 * Note: Requires at least KotNexLib:3.1.2!
 */
object CalendarHelper {

    /**
     * Adds a new event to the specified calendar. Optionally sets the `HAS_ALARM` flag
     * based on [reminderMinutesBeforeEvent].
     *
     * Requires `WRITE_CALENDAR` permission. Runs on the IO dispatcher.
     *
     * @param context The application context.
     * @param calendarId The ID of the target calendar.
     * @param title The title of the event.
     * @param description Optional description for the event.
     * @param location Optional location for the event.
     * @param startTimeMillis Start time in UTC milliseconds since epoch.
     * @param endTimeMillis End time in UTC milliseconds since epoch.
     * @param isAllDay Whether the event is an all-day event.
     * @param reminderMinutesBeforeEvent If provided and non-negative, sets the event's `HAS_ALARM` flag.
     * @return [ResultOf.Success] containing the new event's ID, or [ResultOf.Failure] on error.
     */
    suspend fun addEventToCalendar(
        context: Context,
        calendarId: Long,
        title: String,
        description: String?,
        location: String?,
        startTimeMillis: Long,
        endTimeMillis: Long,
        isAllDay: Boolean = false,
        reminderMinutesBeforeEvent: Int? = null
    ): ResultOf<Long> = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startTimeMillis)
            put(CalendarContract.Events.DTEND, endTimeMillis)
            put(CalendarContract.Events.TITLE, title)
            description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
            location?.let { put(CalendarContract.Events.EVENT_LOCATION, it) }
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

            if (reminderMinutesBeforeEvent != null && reminderMinutesBeforeEvent >= 0) put(CalendarContract.Events.HAS_ALARM, 1)
            else put(CalendarContract.Events.HAS_ALARM, 0)

            if (isAllDay) {
                put(CalendarContract.Events.ALL_DAY, 1)
            } else {
                put(CalendarContract.Events.ALL_DAY, 0)
                put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault().id)
            }
        }

        try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()


            if (reminderMinutesBeforeEvent != null && reminderMinutesBeforeEvent >= 0) {
                val reminderValues = ContentValues().apply {
                    put(Reminders.MINUTES, reminderMinutesBeforeEvent)
                    put(Reminders.EVENT_ID, eventId)
                    put(Reminders.METHOD, Reminders.METHOD_ALERT)
                }
                try {
                    val reminderUri = context.contentResolver.insert(Reminders.CONTENT_URI, reminderValues)
                    if (reminderUri == null) Log.w("CalendarHelper", "Failed to add reminder for event $eventId, insert URI was null.")
                } catch (reminderEx: SecurityException) {
                    ResultOf.Failure("Permission error (write)", reminderEx)
                } catch (reminderEx: Exception) {
                    ResultOf.Failure("General error adding reminder for event $eventId. Event was still created.", reminderEx)
                }
            }

            if (eventId != null) ResultOf.Success(eventId)
            else ResultOf.Failure("Error creating event. EventId is null")
        } catch (e: SecurityException) {
            ResultOf.Failure("Permission error (write)", e)
        } catch (e: Exception) {
            ResultOf.Failure("General error (write)", e)
        }
    }

    /**
     * Data class holding basic information retrieved for a calendar event.
     *
     * @param id The unique ID of the event.
     * @param title The title of the event. Non-null in successful queries by default.
     * @param startTime Start time in UTC milliseconds, nullable.
     * @param endTime End time in UTC milliseconds, nullable.
     */
    data class EventInfo(val id: Long, val title: String, val startTime: Long?, val endTime: Long?)

    /**
     * Reads events from a specific calendar, optionally filtering by a time range.
     * Defaults to reading events starting from 30 days ago.
     *
     * Requires `READ_CALENDAR` permission. Runs on the IO dispatcher.
     *
     * @param context The application context.
     * @param calendarId The ID of the calendar to read from.
     * @param startTimeRange Optional start of the time range (UTC milliseconds). Defaults to 30 days ago.
     * @param endTimeRange Optional end of the time range (UTC milliseconds). If null, no upper bound.
     * @return [ResultOf.Success] containing a list of [EventInfo] objects, or [ResultOf.Failure] on error.
     */
    @SuppressLint("MissingPermission")
    suspend fun readEventsFromCalendar(
        context: Context,
        calendarId: Long,
        startTimeRange: Long? = System.currentTimeMillis() - (30 * 24 * 60).minutes.inWholeMilliseconds, // Optional: Startzeit für Zeitbereichsfilter (in Millis UTC)
        endTimeRange: Long? = null
    ): ResultOf<List<EventInfo>> = withContext(Dispatchers.IO) {
        val eventList = mutableListOf<EventInfo>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        // Filter: Nur Events aus dem ausgewählten Kalender
        var selection = "${CalendarContract.Events.CALENDAR_ID} = ?"
        val selectionArgsList = mutableListOf(calendarId.toString())


        // Optional: Zeitbereichsfilter hinzufügen
        startTimeRange?.let {
            selection += " AND ${CalendarContract.Events.DTSTART} >= ?"
            selectionArgsList.add(it.toString())
        }
        endTimeRange?.let {
            // Wichtig: Suche nach Events, die *innerhalb* des Bereichs liegen oder ihn *überlappen*.
            // Einfache Variante: Events, die *vor* der Endzeit beginnen.
            selection += " AND ${CalendarContract.Events.DTSTART} < ?"
            // Oder komplexer: (DTSTART < end AND DTEND > start)
            // selection += " AND ${CalendarContract.Events.DTEND} > ?" // Startzeit des Events muss vor Endzeit des Bereichs liegen
            selectionArgsList.add(it.toString())
        }

        return@withContext try {
            val cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgsList.toTypedArray(),
                "${CalendarContract.Events.DTSTART} ASC" // Sortierung nach Startzeit
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    do {
                        val eventId: Long = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                        val title: String = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)) ?: continue
                        val dtStart: Long? = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                        val dtEnd: Long? = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                        eventList.add(EventInfo(eventId, title, dtStart, dtEnd))
                    } while (it.moveToNext())
                }
            }
            ResultOf.Success(eventList)
        } catch (e: SecurityException) {
            ResultOf.Failure("Permission error (read)", e)
        } catch (e: Exception) {
            ResultOf.Failure("General error (read)", e)
        }
    }

    /**
     * Deletes a specific event from the calendar using its ID.
     *
     * Requires `WRITE_CALENDAR` permission. Runs on the IO dispatcher.
     *
     * @param context The application context.
     * @param eventId The unique ID of the event to delete.
     * @return `true` if the event was successfully deleted (at least one row affected), `false` otherwise.
     */
    suspend fun deleteEventFromCalendar(context: Context, eventId: Long): Boolean = withContext(Dispatchers.IO) {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        try {
            val rowsDeleted = context.contentResolver.delete(deleteUri, null, null)
            rowsDeleted > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Updates specific fields of an existing calendar event.
     * Only fields with non-null arguments are updated.
     *
     * Requires `WRITE_CALENDAR` permission. Runs on the IO dispatcher.
     *
     * @param context The application context.
     * @param eventId The ID of the event to update.
     * @param newTitle Optional new title.
     * @param newDescription Optional new description.
     * @param newStartTimeMillis Optional new start time (UTC milliseconds).
     * @param newEndTimeMillis Optional new end time (UTC milliseconds).
     * @return `true` if the update was successful (at least one row affected) or if no changes were needed, `false` on error.
     */
    suspend fun updateEventInCalendar(
        context: Context,
        eventId: Long,
        newTitle: String?,
        newDescription: String?,
        newStartTimeMillis: Long?,
        newEndTimeMillis: Long?
    ): ResultOf<Boolean> = withContext(Dispatchers.IO) {
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        val values = ContentValues()
        // Füge nur die Werte hinzu, die tatsächlich geändert werden sollen
        newTitle?.let { values.put(CalendarContract.Events.TITLE, it) }
        newDescription?.let { values.put(CalendarContract.Events.DESCRIPTION, it) }
        newStartTimeMillis?.let { values.put(CalendarContract.Events.DTSTART, it) }
        newEndTimeMillis?.let { values.put(CalendarContract.Events.DTEND, it) }

        if (values.size() == 0) return@withContext ResultOf.Success(true)

        return@withContext try {
            val rowsUpdated = context.contentResolver.update(updateUri, values, null, null)
            ResultOf.Success(rowsUpdated > 0)
        } catch (e: SecurityException) {
            ResultOf.Failure("Permission error (write)", e)
        } catch (e: Exception) {
            ResultOf.Failure("General error (write)", e)
        }
    }

    /**
     * Data class holding information about a calendar account.
     *
     * @param calendarId The unique ID of the calendar, needed for event operations.
     * @param displayName The user-visible name of the calendar.
     * @param accountName The name of the account associated with the calendar.
     */
    data class CalendarInfo(val calendarId: Long, val displayName: String, val accountName: String)

    /**
     * Queries available calendars, filtering for those owned by the user
     * (where OWNER_ACCOUNT matches ACCOUNT_NAME).
     *
     * Requires `READ_CALENDAR` permission. Runs synchronously on the calling thread.
     * Consider running this within a coroutine dispatcher (e.g., Dispatchers.IO) if called from the main thread.
     *
     * @receiver The MainActivity (or Context) to access ContentResolver.
     * @return A list of [CalendarInfo] objects for the user's owned calendars. Returns empty list on error.
     */
    suspend fun queryCalendars(context: Context): ResultOf<List<CalendarInfo>> = withContext(Dispatchers.IO) {

        // Die Spalten, die wir aus der Kalendertabelle benötigen
        val projection: Array<String> = arrayOf(
            CalendarContract.Calendars._ID,                      // Long: Die eindeutige ID des Kalenders
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // String: Der Anzeigename des Kalenders
            CalendarContract.Calendars.ACCOUNT_NAME,            // String: Der Name des Kontos, zu dem der Kalender gehört
            CalendarContract.Calendars.OWNER_ACCOUNT            // String: Der Besitzer-Account (oft gleich wie ACCOUNT_NAME)
            // Optional: CALENDAR_ACCESS_LEVEL, IS_PRIMARY, etc.
        )

        val selection: String? = "(" + CalendarContract.Calendars.ACCOUNT_NAME + " = " + CalendarContract.Calendars.OWNER_ACCOUNT + ")"
        val selectionArgs: Array<String>? = null

        val contentResolver = context.contentResolver

        return@withContext try {
            // Führe die Abfrage aus
            val cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI, // Der URI für Kalender
                projection,
                selection,
                selectionArgs,
                null // Sortierreihenfolge (optional)
            )
            val calendarList = mutableListOf<CalendarInfo>()

            cursor?.use { // 'use' schließt den Cursor automatisch
                if (it.moveToFirst()) {
                    do {
                        val calID: Long = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                        val displayName: String = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                        val accountName: String = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME))


                        calendarList.add(CalendarInfo(calID, displayName, accountName))
                    } while (it.moveToNext())
                }
            } ?: println("Cursor ist null?!?!?!?!")
            ResultOf.Success(calendarList)
        } catch (e: Exception) {
            ResultOf.Failure("Error querying calendars", e)
        }
    }
}
