package com.my.anthonymamode.go4lunch

import android.view.View
import com.my.anthonymamode.go4lunch.domain.Hours
import com.my.anthonymamode.go4lunch.domain.Period
import com.my.anthonymamode.go4lunch.domain.Time
import com.my.anthonymamode.go4lunch.ui.home.list.RestaurantViewHolder
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

    private lateinit var restaurantVH: RestaurantViewHolder
    private lateinit var mock: Hours

    @Before
    fun before() {
        mock = Hours(
            false,
            listOf(
                Period(
                    close = Time(
                        0,
                        "1800"
                    ),
                    open = Time(
                        0,
                        "1200"
                    )
                ),
                Period(
                    close = Time(
                        0,
                        "2300"
                    ),
                    open = Time(
                        0,
                        "1900"
                    )
                ),
                Period(
                    close = Time(
                        1,
                        "1700"
                    ),
                    open = Time(
                        1,
                        "1200"
                    )
                ),
                Period(
                    close = Time(
                        1,
                        "2300"
                    ),
                    open = Time(
                        1,
                        "1900"
                    )
                ),
                Period(
                    close = Time(
                        5,
                        "1100"
                    ),
                    open = Time(
                        5,
                        "1500"
                    )
                ),
                Period(
                    close = Time(
                        5,
                        "2100"
                    ),
                    open = Time(
                        5,
                        "2300"
                    )
                )
            )
        )

        val mockView = Mockito.mock(View::class.java)
        restaurantVH = RestaurantViewHolder(mockView)
    }

    @Test
    fun findTheNextClosingDayTest() {
        // todo: mock static call doesn't work
        /*PowerMockito.mockStatic(Calendar::class.java)
        Mockito.`when`(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).thenReturn(3)*/

        val theNextRestaurantHours = restaurantVH.getTheNextRestaurantHours(mock)

        Assert.assertEquals(mock.periods[5], theNextRestaurantHours)
    }

    @Test
    fun shouldReturnNextOpeningStringTest() {
        val openingText = restaurantVH.getRestaurantOpeningText(mock.periods[0], !mock.open_now)
        Assert.assertEquals("opens dimanche at ${mock.periods[0].open.time}", openingText)
    }
}