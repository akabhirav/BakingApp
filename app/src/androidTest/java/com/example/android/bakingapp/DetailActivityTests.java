package com.example.android.bakingapp;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTests {
    private static final int RECIPE_ID = 1;
    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String RECIPE_FIRST_STEP_DESC = "Recipe Introduction";
    private static final String RECIPE_FIRST_INGREDIENT = "2 CUP Graham Cracker crumbs";
    @Rule
    public ActivityTestRule<DetailActivity> detailActivityActivityTestRule =
            new ActivityTestRule<>(DetailActivity.class, false, false);

    private IdlingResource mIngredientIdlingResource, mStepIdlingResource;
    private IdlingRegistry mIdlingRegistry;

    @Before
    public void initializeActivity(){
        Intent intent = new Intent();
        intent.putExtra("recipe_id", RECIPE_ID);
        intent.putExtra("recipe_name", RECIPE_NAME);
        detailActivityActivityTestRule.launchActivity(intent);
    }

    @Before
    public void registerIdlingResource(){
        mIdlingRegistry =  IdlingRegistry.getInstance();
        mIngredientIdlingResource = detailActivityActivityTestRule.getActivity().mRecipeDetailFragment.getIngredientIdlingResource();
        mStepIdlingResource = detailActivityActivityTestRule.getActivity().mRecipeDetailFragment.getStepIdlingResource();
        mIdlingRegistry.register(mIngredientIdlingResource, mStepIdlingResource);
    }

    @Test
    public void activityTitle_matchesRecipeName(){
        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is((CharSequence) RECIPE_NAME))));
    }
    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    @Test
    public void detailsIdlingResourceTest(){
        onView(new RecyclerViewMatcher(R.id.rv_steps)
                .atPositionOnView(0, R.id.tv_step_desc))
                .check(matches(withText(RECIPE_FIRST_STEP_DESC)));

        onView(new RecyclerViewMatcher(R.id.rv_ingredients)
                .atPositionOnView(0, R.id.tv_ingredient_text))
                .check(matches(withText(RECIPE_FIRST_INGREDIENT)));
    }

    @After
    public void unregisterIdlingResource(){
        if(mIngredientIdlingResource != null)
            mIdlingRegistry.unregister(mIngredientIdlingResource);
        if(mStepIdlingResource != null)
            mIdlingRegistry.unregister(mStepIdlingResource);
    }
}
