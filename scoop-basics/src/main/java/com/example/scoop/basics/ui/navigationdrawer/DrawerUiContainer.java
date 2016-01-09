package com.example.scoop.basics.ui.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;

import com.example.scoop.basics.rx.ViewSubscriptions;
import com.example.scoop.basics.scoop.DaggerInjector;
import com.example.scoop.basics.scoop.DaggerViewControllerInflater;
import com.example.scoop.basics.ui.Keyboard;
import com.lyft.scoop.RouteChange;
import com.lyft.scoop.UiContainer;
import com.lyft.scoop.ViewControllerInflater;

import javax.inject.Inject;

import rx.functions.Action1;
import timber.log.Timber;

public class DrawerUiContainer extends UiContainer {

    @Inject
    ScreensRouter screensRouter;

    private ViewSubscriptions subscriptions = new ViewSubscriptions();

    public DrawerUiContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        DaggerInjector.fromView(this).inject(this);
    }

    @Override
    protected ViewControllerInflater getViewControllerInflater() {
        return new DaggerViewControllerInflater();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }

        subscriptions.add(screensRouter.observeScreenChange(), onScreenChanged);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        subscriptions.unsubscribe();
    }

    private Action1<RouteChange> onScreenChanged = new Action1<RouteChange>() {
        @Override
        public void call(RouteChange screenChange) {
            Timber.d("Scoop changed:" + screenChange.next.getController().getSimpleName());
            DrawerUiContainer.this.goTo(screenChange);
            Keyboard.hideKeyboard(DrawerUiContainer.this);
        }
    };
}
