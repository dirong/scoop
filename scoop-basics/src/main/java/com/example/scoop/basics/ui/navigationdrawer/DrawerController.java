package com.example.scoop.basics.ui.navigationdrawer;

import android.app.Activity;
import android.app.Application;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.scoop.basics.MainActivity;
import com.example.scoop.basics.R;
import com.example.scoop.basics.scoop.AppRouter;
import com.example.scoop.basics.scoop.ControllerModule;
import com.example.scoop.basics.scoop.DaggerScreenScooper;
import com.example.scoop.basics.ui.DemosController;
import com.lyft.scoop.EnterTransition;
import com.lyft.scoop.ExitTransition;
import com.lyft.scoop.ParentController;
import com.lyft.scoop.RouteChange;
import com.lyft.scoop.Scoop;
import com.lyft.scoop.Screen;
import com.lyft.scoop.TransitionDirection;
import com.lyft.scoop.ViewController;
import com.lyft.scoop.transitions.BackwardSlideTransition;
import com.lyft.scoop.transitions.ForwardSlideTransition;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.Bind;
import dagger.Provides;

/**
 * Created by dirong on 12/15/15.
 */
@ParentController(DemosController.class)
@EnterTransition(ForwardSlideTransition.class)
@ExitTransition(BackwardSlideTransition.class)
@ControllerModule(DrawerController.Module.class)
public class DrawerController extends ViewController {

    private final Application application;
    private final AppRouter appRouter;
    private final Activity activity;
    private final ScreensRouter screensRouter;
    private Scoop screenScoop;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @dagger.Module(
            injects = {
                    DrawerController.class,
                    DrawerUiContainer.class,
            },
            addsTo = DemosController.Module.class,
            library = true
    )
    public static class Module {}

    public static Screen createScreen() {
        return Screen.create(DrawerController.class);
    }


    @Inject
    public DrawerController(Application application, Activity activity, AppRouter appRouter, ScreensRouter screensRouter) {
        this.appRouter = appRouter;
        this.application = application;
        this.activity = activity;
        this.screensRouter = screensRouter;
    }

    @Override
    protected int layoutId() {
        return R.layout.home_drawer_layout;
    }

    private Scoop getScreenScoop() {
        if (screenScoop == null) {
            screenScoop = new Scoop.Builder("screens", getScoop()).build();
        }
        return screenScoop;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                screensRouter.goTo(ParametrizedController.createScreen(menuItem.getTitle().toString()));
                drawer.closeDrawer(Gravity.LEFT);
                return false;
            }
        });
        MenuItem firstItem = navigationView.getMenu().getItem(0);
        firstItem.setChecked(true);
        screensRouter.onCreate(getScreenScoop(), ParametrizedController.createScreen(firstItem.getTitle().toString()));
    }
}
