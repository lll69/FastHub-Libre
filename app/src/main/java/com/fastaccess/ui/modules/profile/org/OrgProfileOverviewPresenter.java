package com.fastaccess.ui.modules.profile.org;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 04 Apr 2017, 10:36 AM
 */

public class OrgProfileOverviewPresenter extends BasePresenter<OrgProfileOverviewMvp.View> implements OrgProfileOverviewMvp.Presenter {
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State boolean isSuccessResponse;
    @com.evernote.android.state.State boolean isFollowing;

    @Override public void onError(@NonNull Throwable throwable) {
        if (!InputHelper.isEmpty(login)) {
            onWorkOffline(login);
        }
        super.onError(throwable);
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null || bundle.getString(BundleConstant.EXTRA) == null) {
            throw new NullPointerException("Either bundle or User is null");
        }
        login = bundle.getString(BundleConstant.EXTRA);
        if (login != null) {
            makeRestCall(RestProvider.getOrgService(isEnterprise()).getOrganization(login),
                    userModel -> {
                onSendUserToView(userModel);
                onCheckFollowStatus(login);
            });
        }
    }

    @Override public void onWorkOffline(@NonNull String login) {
        onSendUserToView(User.getUser(login));
    }

    @NonNull @Override public String getLogin() {
        return login;
    }

    private void onSendUserToView(User userModel) {
        sendToView(view -> view.onInitViews(userModel));
    }

    @Override public void onCheckFollowStatus(@NonNull String login) {
        if (!TextUtils.equals(login, Login.getUser().getLogin())) {
            manageDisposable(RxHelper.getObservable(RestProvider.getUserService(isEnterprise()).getFollowStatus(login))
                    .subscribe(booleanResponse -> {
                        isSuccessResponse = true;
                        isFollowing = booleanResponse.code() == 204;
                        sendToView(OrgProfileOverviewMvp.View::invalidateFollowBtn);
                    }, Throwable::printStackTrace));
        }
    }

    @Override public boolean isSuccessResponse() {
        return isSuccessResponse;
    }

    @Override public boolean isFollowing() {
        return isFollowing;
    }

    @Override public void onFollowButtonClicked(@NonNull String login) {
        manageDisposable(RxHelper.getObservable(!isFollowing ? RestProvider.getUserService(isEnterprise()).followUser(login)
                : RestProvider.getUserService(isEnterprise()).unfollowUser(login))
                .subscribe(booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        isFollowing = !isFollowing;
                        sendToView(OrgProfileOverviewMvp.View::invalidateFollowBtn);
                    }
                }, this::onError));
    }
}
