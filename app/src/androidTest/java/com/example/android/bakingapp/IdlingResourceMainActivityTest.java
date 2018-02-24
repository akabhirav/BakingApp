package com.example.android.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private IdlingRegistry mIdlingRegistry;

    @Before
    public void registerIdlingResource(){
        mIdlingRegistry =  IdlingRegistry.getInstance();
        mIdlingResource = mainActivityActivityTestRule.getActivity().getIdlingResource();
        mIdlingRegistry.register(mIdlingResource);
    }

    @Test
    public void recipesIdlingResourceTest(){
        onView(withId(R.id.rv_recipes)).perform(actionOnItemAtPosition(1, click()));
    }

    @After
    public void unregisterIdlingResource(){
        if(mIdlingResource != null)
            mIdlingRegistry.unregister(mIdlingResource);
    }
}
