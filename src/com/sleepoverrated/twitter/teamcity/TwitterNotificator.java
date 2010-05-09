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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
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
    private static final String TWITTER_BUILD_STARTED = "Build Started";
    private static final String TWITTER_BUILD_SUCCESSFUL = "Build Successful";
    private static final String TWITTER_BUILD_FAILED = "Build Failure";
    private static final String TWITTER_BUILD_LABELING_FAILED = "Labeling Failed";
    private static final String TWITTER_BUILD_FAILING = "Build Failing";
    private static final String TWITTER_BUILD_HANGING = "Build Hanging";
    private static final String TWITTER_BUILD_RESPONSIBILITY_CHANGED = "Responsibility Changed";
    private static final PropertyKey USER_ID = new NotificatorPropertyKey(TYPE, TWITTER_USER_ID);
    private static final PropertyKey PASSWORD = new NotificatorPropertyKey(TYPE, TWITTER_PASSWORD);
    private static final PropertyKey BUILD_STARTED = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_STARTED);
    private static final PropertyKey BUILD_SUCCESSFUL = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_SUCCESSFUL);
    private static final PropertyKey BUILD_FAILED = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_FAILED);
    private static final PropertyKey BUILD_LABELING_FAILED = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_LABELING_FAILED);
    private static final PropertyKey BUILD_FAILING = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_FAILING);
    private static final PropertyKey BUILD_HANGING = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_HANGING);
    private static final PropertyKey BUILD_RESPONSIBILITY_CHANGED = new NotificatorPropertyKey(TYPE, TWITTER_BUILD_RESPONSIBILITY_CHANGED);

    public TwitterNotificator(NotificatorRegistry notificatorRegistry) throws IOException {
        ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(TWITTER_USER_ID, "Twitter Username"));
        userProps.add(new UserPropertyInfo(TWITTER_PASSWORD, "Twitter Password"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_STARTED, "Build started"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_SUCCESSFUL, "Build successful"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_FAILED, "Build failed"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_LABELING_FAILED, "Build labeling failed"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_FAILING, "Build failing"));
        userProps.add(new UserPropertyInfo(TWITTER_BUILD_RESPONSIBILITY_CHANGED, "Responsibility changed"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_STARTED, "#BUILDNAME# started"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_SUCCESSFUL, "#BUILDNAME# #BUILDNUMBER# successful"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_FAILED, "#BUILDNAME# #BUILDNUMBER# failed"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_LABELING_FAILED, "#BUILDNAME# #BUILDNUMBER# labeling failed"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_FAILING, "#BUILDNAME# #BUILDNUMBER# failing"));
        //userProps.add(new UserPropertyInfo(TWITTER_BUILD_RESPONSIBILITY_CHANGED, "#BUILDNAME# responsibility changed"));
        notificatorRegistry.register(this, userProps);
    }

    public String formatMessage(Properties properties, String message) {
        for (Object key : properties.keySet()) {
            message = message.replaceAll((String) key, properties.getProperty((String) key));
        }
        return message;
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sRunningBuild.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sRunningBuild.getFullName());
        props.setProperty("#BUILDNUMBER#", sRunningBuild.getBuildNumber());
        if(sRunningBuild.getRevisions().size()>0)
            props.setProperty("#VCSNUMBER#", sRunningBuild.getRevisions().get(0).getRevisionDisplayName());
        else
            props.setProperty("#VCSNUMBER#", "");
        System.out.println("Build started");
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_STARTED)), user);
        }
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sRunningBuild.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sRunningBuild.getFullName());
        if(sRunningBuild.getRevisions().size()>0)
            props.setProperty("#VCSNUMBER#", sRunningBuild.getRevisions().get(0).getRevisionDisplayName());
        else
            props.setProperty("#VCSNUMBER#", "");
        props.setProperty("#BUILDNUMBER#", sRunningBuild.getBuildNumber());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_SUCCESSFUL)), user);
        }
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sRunningBuild.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sRunningBuild.getFullName());
        if(sRunningBuild.getRevisions().size()>0)
            props.setProperty("#VCSNUMBER#", sRunningBuild.getRevisions().get(0).getRevisionDisplayName());
        else
            props.setProperty("#VCSNUMBER#", "");
        props.setProperty("#BUILDNUMBER#", sRunningBuild.getBuildNumber());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_FAILED)), user);
        }
    }

    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", build.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", build.getFullName());
        props.setProperty("#VCSNUMBER#", "");
        props.setProperty("#BUILDNUMBER#", build.getBuildNumber());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_LABELING_FAILED)), user);
        }
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sRunningBuild.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sRunningBuild.getFullName());
        if(sRunningBuild.getRevisions().size()>0)
            props.setProperty("#VCSNUMBER#", sRunningBuild.getRevisions().get(0).getRevisionDisplayName());
        else
            props.setProperty("#VCSNUMBER#", "");
        props.setProperty("#BUILDNUMBER#", sRunningBuild.getBuildNumber());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_FAILING)), user);
        }

    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sRunningBuild.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sRunningBuild.getFullName());
        if(sRunningBuild.getRevisions().size()>0)
            props.setProperty("#VCSNUMBER#", sRunningBuild.getRevisions().get(0).getRevisionDisplayName());
        else
            props.setProperty("#VCSNUMBER#", "");
        props.setProperty("#BUILDNUMBER#", sRunningBuild.getBuildNumber());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_HANGING)), user);
        }
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        Properties props = new Properties();
        props.setProperty("#BUILDNAME#", sBuildType.getFullName().split("::")[1]);
        props.setProperty("#FULLNAME#", sBuildType.getFullName());
        for (SUser user : sUsers) {
            doNotifications(formatMessage(props, user.getPropertyValue(BUILD_RESPONSIBILITY_CHANGED)), user);
        }
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void doNotifications(String message, SUser user) {
        new TwitterGateway()
                .setUsername(user.getPropertyValue(USER_ID))
                .setPassword(user.getPropertyValue(PASSWORD))
                .sendTweet(message);
    }
}
