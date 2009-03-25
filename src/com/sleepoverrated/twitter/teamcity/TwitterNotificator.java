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
 * This teamcity plugins is configured as a new notificator. The notifications are text
 * messages, which are sended to the text to speech engine of the Nabaztag.
 * For each teamcity user it is possible to configure his Nabaztag settings and notification events. In this
 * way multiple Nabaztags could be accessed for different projects or developers.
 * The configuration could be found in the same place, where mail or ide notifications are configured.
 *
 * User: Simon Tiffert
 * Copyright: Agimatec GmbH 2008 
 */
public class TwitterNotificator implements Notificator {

    private static final String TYPE = "twitterNotifier";
    private static final String TYPE_NAME = "Twitter Notifier";
    private static final String TWITTER_USER = "Twitter Id";
    private static final String TWITTER_PASS = "Twitter Password";


    public TwitterNotificator(NotificatorRegistry notificatorRegistry) throws IOException {
        ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(TWITTER_USER, "Twitter Username"));
        userProps.add(new UserPropertyInfo(TWITTER_PASS, "Twitter Password"));
        notificatorRegistry.register(this, userProps);
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " started.",sUsers);
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " successfull.",sUsers);
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " failed.",sUsers);
    }

    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        doNotifications("Labeling of build " + build.getFullName() + " failed.",sUsers);
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " is failing.",sUsers);
    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " is probably hanging.",sUsers);
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        doNotifications("Responsibility of build " + sBuildType.getFullName() + " changed.",sUsers);
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void doNotifications(String message, Set<SUser> sUsers) {
        for(SUser user : sUsers) {
            Api api = Api.builder().username(user.getProperties().get(TWITTER_USER)).password(user.getProperties().get(TWITTER_PASS)).build();
            api.updateStatus(message).build().post();
        }
    }
}
