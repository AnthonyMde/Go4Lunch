package com.my.anthonymamode.go4lunch

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.my.anthonymamode.go4lunch.domain.Hours
import com.my.anthonymamode.go4lunch.domain.Period
import com.my.anthonymamode.go4lunch.domain.Time
import com.my.anthonymamode.go4lunch.ui.home.list.getRestaurantOpeningText
import com.my.anthonymamode.go4lunch.ui.home.list.getTheNextRestaurantHours
import com.my.anthonymamode.go4lunch.ui.home.list.getTimeInMinutes
import com.my.anthonymamode.go4lunch.ui.home.list.googleTimeIntoPair
import com.my.anthonymamode.go4lunch.utils.addChar
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class OpeningHoursUnitTest {

    private lateinit var context: Context
    private lateinit var mockForOpening: Hours
    private lateinit var mockForClosing: Hours

    @get:Rule
    val exception: ExpectedException = ExpectedException.none()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context

        mockForOpening = Hours(
            false,
            listOf(
                Period(
                    closeTime = Time(
                        0,
                        "1800"
                    ),
                    openTime = Time(
                        0,
                        "1200"
                    )
                ),
                Period(
                    closeTime = Time(
                        0,
                        "2300"
                    ),
                    openTime = Time(
                        0,
                        "1900"
                    )
                ),
                Period(
                    closeTime = Time(
                        1,
                        "1700"
                    ),
                    openTime = Time(
                        1,
                        "1200"
                    )
                ),
                Period(
                    closeTime = Time(
                        2,
                        "0000"
                    ),
                    openTime = Time(
                        1,
                        "1900"
                    )
                ),
                Period(
                    closeTime = Time(
                        5,
                        "1100"
                    ),
                    openTime = Time(
                        5,
                        "1500"
                    )
                ),
                Period(
                    closeTime = Time(
                        5,
                        "0600"
                    ),
                    openTime = Time(
                        5,
                        "0000"
                    )
                )
            )
        )

        mockForClosing = Hours(
            true,
            listOf(
                Period(
                    closeTime = Time(
                        0,
                        "1800"
                    ),
                    openTime = Time(
                        0,
                        "1200"
                    )
                ),
                Period(
                    closeTime = Time(
                        0,
                        "2300"
                    ),
                    openTime = Time(
                        0,
                        "1900"
                    )
                ),
                Period(
                    closeTime = Time(
                        1,
                        "1700"
                    ),
                    openTime = Time(
                        1,
                        "1200"
                    )
                ),
                Period(
                    closeTime = Time(
                        2,
                        "0000"
                    ),
                    openTime = Time(
                        1,
                        "1900"
                    )
                ),
                Period(
                    closeTime = Time(
                        5,
                        "1100"
                    ),
                    openTime = Time(
                        5,
                        "1500"
                    )
                ),
                Period(
                    closeTime = Time(
                        5,
                        "0600"
                    ),
                    openTime = Time(
                        5,
                        "0000"
                    )
                )
            )
        )
    }

    @Test
    fun findTheNextOpeningDayTest() {
        // Classic one, the next opening is another day
        Assert.assertEquals(
            mockForOpening.periods[5],
            getTheNextRestaurantHours(mockForOpening, 3, 10, 0)
        )

        // The next opening is in the same day : should find the afternoon opening, not the morning one
        Assert.assertEquals(
            mockForOpening.periods[1],
            getTheNextRestaurantHours(mockForOpening, 0, 15, 0)
        )

        // The next opening is midnight (next day) == edge case
        Assert.assertEquals(
            mockForOpening.periods[5],
            getTheNextRestaurantHours(mockForOpening, 4, 19, 0)
        )
    }

    @Test
    fun findTheNextClosingDayTest() {
        // The next closing is in the same day
        Assert.assertEquals(
            mockForClosing.periods[1],
            getTheNextRestaurantHours(mockForClosing, 0, 21, 0)
        )

        // The next closing is midnight (next day) == edge case
        Assert.assertEquals(
            mockForClosing.periods[3],
            getTheNextRestaurantHours(mockForClosing, 1, 21, 0)
        )
    }

    @Test
    fun shouldReturnNextOpeningStringTest() {
        // Should print the day name and opening
        var openingText = getRestaurantOpeningText(mockForOpening.periods[0], true, 3, context)
        Assert.assertEquals(
            "Opens sunday at ${mockForOpening.periods[0].openTime.time.addChar(':', 2)}",
            openingText
        )

        // If current day == target day -> should not print day name, just time
        openingText = getRestaurantOpeningText(mockForOpening.periods[0], true, 0, context)
        Assert.assertEquals(
            "Opens at ${mockForOpening.periods[0].openTime.time.addChar(':', 2)}",
            openingText
        )

        // If we have no period && we search next closing -> restaurant is always open
        openingText = getRestaurantOpeningText(null, false, 0, context)
        Assert.assertEquals("Opened 24/7", openingText)
    }

    @Test
    fun shouldReturnNextClosingStringTest() {
        // Should print the day name and closing
        var openingText = getRestaurantOpeningText(mockForOpening.periods[0], false, 3, context)
        Assert.assertEquals(
            "Closes sunday at ${mockForOpening.periods[0].closeTime.time.addChar(':', 2)}",
            openingText
        )

        // If current day == target day -> should not print day name, just time
        openingText = getRestaurantOpeningText(mockForOpening.periods[0], false, 0, context)
        Assert.assertEquals(
            "Closes at ${mockForOpening.periods[0].closeTime.time.addChar(':', 2)}",
            openingText
        )

        // If we have no period && we search next opening -> restaurant is closed permanently
        openingText = getRestaurantOpeningText(null, true, 0, context)
        Assert.assertEquals("Permanently closed", openingText)
    }

    @Test
    fun hoursAndMinutesShouldBeConvertedIntoMinutes() {
        val inMinutes = getTimeInMinutes(Pair(3, 50))
        Assert.assertEquals((3 * 60) + 50, inMinutes)
    }

    @Test
    fun givenWrongHours_whenConvertingIntoMinutes_thenThrowException() {
        getTimeInMinutes(Pair(0, 30)) // should work
        getTimeInMinutes(Pair(24, 30)) // should work
        exception.expect(IllegalArgumentException::class.java)
        getTimeInMinutes(Pair(25, 0)) // should throw
    }

    @Test
    fun givenWrongMinutes_whenConvertingIntoMinutes_thenThrowException() {
        getTimeInMinutes(Pair(10, 0)) // should work
        getTimeInMinutes(Pair(10, 60)) // should work
        exception.expect(IllegalArgumentException::class.java)
        getTimeInMinutes(Pair(1, 61)) // should throw
    }

    @Test
    fun shouldConvertGoogleTime() {
        val googleTimes = listOf("1030", "2200", "0930", "0030", "0000")
        val convertedTimes = listOf(Pair(10, 30), Pair(22, 0), Pair(9, 30), Pair(0, 30), Pair(0, 0))

        googleTimes.forEachIndexed { index, googleTime ->
            Assert.assertEquals(convertedTimes[index], googleTimeIntoPair(googleTime))
        }
    }
}
