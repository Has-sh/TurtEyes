package com.example.myapplication;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.myapplication.statsActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class statsActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<statsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(statsActivity.class);

    @Test
    public void testDataLoadingText() {
        onView(withId(R.id.gpsData)).check(matches(withText("Location: loading data")));
        onView(withId(R.id.hatchlingData)).check(matches(withText("Hatchling Status: loading data")));
    }
}
