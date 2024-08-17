package com.example.myapplication;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class LaunchTest{

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

//    @Test
////    public void appLaunchesSuccessfully() {
////        // Check that the app launches successfully
////        Espresso.onView(ViewMatchers.withId(R.id.mainLayout))
////                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
////    }
}

