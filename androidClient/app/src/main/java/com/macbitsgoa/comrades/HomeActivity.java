package com.macbitsgoa.comrades;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.macbitsgoa.comrades.aboutmac.AboutMacActivity;
import com.macbitsgoa.comrades.courselistfragment.CourseListFragment;
import com.macbitsgoa.comrades.homefragment.HomeFragment;
import com.macbitsgoa.comrades.persistance.Database;
import com.macbitsgoa.comrades.profilefragment.ProfileFragment;
import com.macbitsgoa.comrades.search.SearchCoursesCursorAdapter;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


public class HomeActivity extends AppCompatActivity {
    public static final String SETTINGS = "NotificationSetting";
    public static final String TAG_HOME_FRAG = "HomeFragment";
    public static final String TAG_COURSE_LIST_FRAG = "CourseListFragment";
    public static final String TAG_PROFILE_FRAG = "ProfileFragment";
    public static final String TAG = TAG_PREFIX + HomeActivity.class.getSimpleName();
    public static BottomNavigationView navigation;
    public static CoordinatorLayout snack;
    private GoogleApiClient mGoogleApiClient;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SearchView searchView;
    private MySimpleDraweeView userProfileImage;
    private FloatingActionButton fab_add_course;
    private Screens currentScreen = Screens.HOME;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fab_add_course.setVisibility(View.GONE);
                fab_add_course.setOnClickListener(null);
                Fragment homeFragment = fragmentManager.findFragmentByTag(TAG_HOME_FRAG);
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance();
                }
                fragmentManager.beginTransaction().replace(R.id.container_fragment,
                        homeFragment, TAG_HOME_FRAG).addToBackStack(null).commit();
                currentScreen = Screens.HOME;
                break;
            case R.id.navigation_courses:
                fab_add_course.setVisibility(View.VISIBLE);
                fab_add_course.setOnClickListener(v -> CourseListFragment.handleAddCourse(HomeActivity.this));
                Fragment courseListFragment = fragmentManager.findFragmentByTag(TAG_COURSE_LIST_FRAG);
                if (courseListFragment == null) {
                    courseListFragment = CourseListFragment.newInstance();
                }
                fragmentManager.beginTransaction().replace(R.id.container_fragment,
                        courseListFragment, TAG_COURSE_LIST_FRAG).addToBackStack(null).commit();
                currentScreen = Screens.COURSE_LIST;
                break;
            case R.id.navigation_profile:
                fab_add_course.setVisibility(View.GONE);
                fab_add_course.setOnClickListener(null);
                Fragment profileFragment = fragmentManager.findFragmentByTag(TAG_PROFILE_FRAG);
                if (profileFragment == null) {
                    profileFragment = ProfileFragment.newInstance();
                }
                fragmentManager.beginTransaction().replace(R.id.container_fragment,
                        profileFragment, TAG_PROFILE_FRAG).addToBackStack(null).commit();
                currentScreen = Screens.PROFILE;
                break;
            default:
                return false;
        }
        invalidateOptionsMenu();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.toolbar_main_act));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        userProfileImage = findViewById(R.id.profile_user_toolbar);
        navigation = findViewById(R.id.navigation);
        snack = findViewById(R.id.container);
        fab_add_course = findViewById(R.id.fab_add_course);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.addOnBackStackChangedListener(getListener());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // Check if we need to display our OnboardingFragment
        if (!sharedPreferences.getBoolean(
                TutorialActivity.COMPLETED_ON_BOARDING_PREF_NAME, false)) {
            // The user hasn't seen the OnboardingFragment yet, so show it
            startActivity(new Intent(this, TutorialActivity.class));
        }

        if (savedInstanceState != null) {
            navigation.setSelectedItemId(savedInstanceState.getInt("bottomNav"));
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }

    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("bottomNav", navigation.getSelectedItemId());
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        return () -> {
            if (fragmentManager != null) {
                Fragment currFrag = fragmentManager.findFragmentByTag(TAG_PROFILE_FRAG);
                if (navigation.getSelectedItemId() == R.id.navigation_profile && currFrag != null) {
                    currFrag.onResume();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_toolbar, menu);
        final boolean signedIn = GoogleSignIn.getLastSignedInAccount(this) != null;
        final MenuItem signOut = menu.findItem(R.id.action_sign_out);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userProfileImage.setParam(user.getUid());
            userProfileImage.setImageURI(user.getPhotoUrl());
        } else {
            userProfileImage.setParam(null);
            userProfileImage.setImageResource(R.drawable.ic_profile_white);
        }
        signOut.setVisible(signedIn);
        signOut.setOnMenuItemClickListener(menuItem -> {
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                invalidateOptionsMenu();
                Toast.makeText(HomeActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                navigation.setSelectedItemId(navigation.getSelectedItemId());
            });
            return true;
        });

        MenuItem search = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) search.getActionView();
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search.." + "</font>"));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("TAG", "query:" + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("TAG", "query:" + s);
                getCoursesFromDb(s);
                return false;
            }
        });

        final MenuItem sort = menu.findItem(R.id.action_sort);
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(final MenuItem menuItem) {
                sort.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem menuItem) {
                invalidateOptionsMenu();
                return true;
            }
        });

        final MenuItem aboutMac = menu.findItem(R.id.action_about_mac);
        aboutMac.setOnMenuItemClickListener(menuItem1 -> {
            startActivity(new Intent(this, AboutMacActivity.class));
            return true;

        });

        if (currentScreen == Screens.COURSE_LIST) {
            sort.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem menuItem) {
                    Fragment fragment = fragmentManager.findFragmentByTag(TAG_COURSE_LIST_FRAG);
                    if (fragment != null && fragment instanceof CourseListFragment) {
                        ((CourseListFragment) fragment).handleSort();
                    } else {
                        Log.e(TAG, "sort icon was shown but, currently not in course list fragment");
                    }
                    return true;
                }
            });
        } else {
            sort.setVisible(false);
        }
        return true;

    }

    @SuppressLint("CheckResult")
    private void getCoursesFromDb(String query) {
        String searchText = "%" + query + "%";
        Observable.just(searchText).observeOn(Schedulers.computation())
                .map(searchStrt -> Database.getInstance(HomeActivity.this).getCourseDao().getSearchCursor(searchStrt)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError);

    }

    private void handleResults(Cursor cursor) {
        searchView.setSuggestionsAdapter(new SearchCoursesCursorAdapter(HomeActivity.this, cursor));
    }

    private void handleError(Throwable t) {
        Log.e("TAG", t.getMessage(), t);
        Toast.makeText(this, "Problem in Fetching Courses",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_home) {
            finish();
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previousStarted = preferences.getBoolean("Previously Started", false);

        if (!previousStarted) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(SETTINGS, true);
            edit.putBoolean("Previously Started", Boolean.TRUE);
            edit.apply();
        }
    }

    public enum Screens {
        HOME,
        COURSE_LIST,
        PROFILE
    }
}
