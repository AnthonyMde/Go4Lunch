package com.my.anthonymamode.go4lunch

import android.view.View
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.domain.Hours
import com.my.anthonymamode.go4lunch.domain.Period
import com.my.anthonymamode.go4lunch.domain.Time
import com.my.anthonymamode.go4lunch.ui.home.list.RestaurantAdapter
import com.my.anthonymamode.go4lunch.utils.addChar
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.Calendar

@RunWith(PowerMockRunner::class)
@PrepareForTest(Calendar::class)
class OpeningHoursUnitTest {

    private lateinit var restaurantVH: RestaurantAdapter.RestaurantViewHolder
    private lateinit var mockForOpening: Hours
    private lateinit var mockForClosing: Hours

    @Before
    fun before() {
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

        val mockView = Mockito.mock(View::class.java)
        val localizationMock = LatLng(43.6043, 1.4437)
        val uidMock = "1"
        restaurantVH =
            RestaurantAdapter(uidMock, localizationMock) {}.RestaurantViewHolder(mockView)
    }

    @Test
    fun findTheNextOpeningDayTest() {
        // Classic one, the next opening is another day
        restaurantVH.setCurrentDayAndHour(3, 10)
        Assert.assertEquals(
            mockForOpening.periods[5],
            restaurantVH.getTheNextRestaurantHours(mockForOpening)
        )

        // The next opening is in the same day : should find the afternoon opening, not the morning one
        restaurantVH.setCurrentDayAndHour(0, 15)
        Assert.assertEquals(
            mockForOpening.periods[1],
            restaurantVH.getTheNextRestaurantHours(mockForOpening)
        )

        // The next opening is midnight (next day) == edge case
        restaurantVH.setCurrentDayAndHour(4, 19)
        Assert.assertEquals(
            mockForOpening.periods[5],
            restaurantVH.getTheNextRestaurantHours(mockForOpening)
        )
    }

    @Test
    fun findTheNextClosingDayTest() {
        // The next closing is in the same day
        restaurantVH.setCurrentDayAndHour(0, 21)
        Assert.assertEquals(
            mockForClosing.periods[1],
            restaurantVH.getTheNextRestaurantHours(mockForClosing)
        )

        // The next closing is midnight (next day) == edge case
        restaurantVH.setCurrentDayAndHour(1, 21)
        Assert.assertEquals(
            mockForClosing.periods[3],
            restaurantVH.getTheNextRestaurantHours(mockForClosing)
        )
    }

    @Test
    fun shouldReturnNextOpeningStringTest() {
        // Should print the day name and opening
        restaurantVH.setCurrentDayAndHour(3, 10)
        var openingText = restaurantVH.getRestaurantOpeningText(mockForOpening.periods[0], true)
        Assert.assertEquals(
            "opens dimanche at ${mockForOpening.periods[0].openTime.time.addChar(':', 2)}",
            openingText
        )

        // If current day == target day -> should not print day name, just time
        restaurantVH.setCurrentDayAndHour(0, 10)
        openingText = restaurantVH.getRestaurantOpeningText(mockForOpening.periods[0], true)
        Assert.assertEquals("opens at ${mockForOpening.periods[0].openTime.time.addChar(':', 2)}", openingText)

        // If we have no period && we search next closing -> restaurant is always open
        openingText = restaurantVH.getRestaurantOpeningText(null, false)
        Assert.assertEquals("opened 24/7", openingText)
    }

    @Test
    fun shouldReturnNextClosingStringTest() {
        // Should print the day name and closing
        restaurantVH.setCurrentDayAndHour(3, 10)
        var openingText = restaurantVH.getRestaurantOpeningText(mockForOpening.periods[0], false)
        Assert.assertEquals(
            "closes dimanche at ${mockForOpening.periods[0].closeTime.time.addChar(':', 2)}",
            openingText
        )

        // If current day == target day -> should not print day name, just time
        restaurantVH.setCurrentDayAndHour(0, 10)
        openingText = restaurantVH.getRestaurantOpeningText(mockForOpening.periods[0], false)
        Assert.assertEquals("closes at ${mockForOpening.periods[0].closeTime.time.addChar(':', 2)}", openingText)

        // If we have no period && we search next opening -> restaurant is closed permanently
        openingText = restaurantVH.getRestaurantOpeningText(null, true)
        Assert.assertEquals("permanently closed", openingText)
    }
}
