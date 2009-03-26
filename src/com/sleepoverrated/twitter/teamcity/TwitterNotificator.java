package com.sleepoverrated.twitter.teamcity;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;
import net.unto.twitter.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * This teamcity plugin lets you tweet to a twitter account per user based on
 * your notification settings. The only thing you can currently configure is the
 * twitter account being used in the 'My Settings and Tools' section.
 *
 * Scott Cowan
 */
public class TwitterNotificator implements Notificator {

    private static final String TYPE = "twitterNotifier";
    private static final String TYPE_NAME = "Twitter Notifier";
    private static final String TWITTER_USER_ID = "Twitter Id";
    private static final String TWITTER_PASSWORD = "Twitter Password";

    private static final PropertyKey USER_ID = new NotificatorPropertyKey(TYPE, TWITTER_USER_ID);
    private static final PropertyKey PASSWORD = new NotificatorPropertyKey(TYPE, TWITTER_PASSWORD);

    public TwitterNotificator(NotificatorRegistry notificatorRegistry) throws IOException {
        ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(TWITTER_USER_ID, "Twitter Username"));
        userProps.add(new UserPropertyInfo(TWITTER_PASSWORD, "Twitter Password"));
        notificatorRegistry.register(this, userProps);
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications(sRunningBuild.getFullName() + " started.",sUsers);
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications(sRunningBuild.getFullName() + " successfull.",sUsers);
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications(sRunningBuild.getFullName() + " failed.",sUsers);
    }

    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        doNotifications(build.getFullName() + " Labelling failed.",sUsers);
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications(sRunningBuild.getFullName() + " is failing.",sUsers);
    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications(sRunningBuild.getFullName() + " is probably hanging.",sUsers);
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        doNotifications(sBuildType.getFullName() + " Responsibility changed.",sUsers);
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void doNotifications(String message, Set<SUser> sUsers) {     
        for(SUser user : sUsers) {
            Api api = Api.builder().username(user.getPropertyValue(USER_ID) ).password(user.getPropertyValue(PASSWORD)).build();
            api.updateStatus(message).build().post();
        }
    }
}
