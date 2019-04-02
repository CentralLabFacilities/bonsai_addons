package de.unibi.citec.clf.bonsai.ros.helper;

import actionlib_msgs.GoalStatus;
import de.unibi.citec.clf.btl.util.GarbageGraspResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import garbage_grasping_msgs.*;

import com.github.rosjava_actionlib.ActionFuture;

public class GarbageFuture implements Future<GarbageGraspResult> {

    ActionFuture<garbageActionGoal, garbageActionFeedback, garbageActionResult> action;

    public GarbageFuture(ActionFuture<garbageActionGoal, garbageActionFeedback, garbageActionResult> f) {
        action = f;
    }

    @Override
    public boolean cancel(boolean b) {
        return action.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        return action.isCancelled();
    }

    @Override
    public boolean isDone() {
        return action.isDone();
    }

    @Override
    public GarbageGraspResult get() throws InterruptedException, ExecutionException {
        return toGarbageGraspResult(action.get());
    }

    @Override
    public GarbageGraspResult get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return toGarbageGraspResult(action.get(l,timeUnit));
    }

    private static GarbageGraspResult toGarbageGraspResult(garbageActionResult res) {

        if (res.getResult().getReturnCode().getCode() == returnCode.SUCCESS) {
            return new GarbageGraspResult(GarbageGraspResult.Result.SUCCESS, 0);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.NAV_FAILED) {
            return new GarbageGraspResult(GarbageGraspResult.Result.NAV_FAILED, 3);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.PLAN_FAILED) {
            return new GarbageGraspResult(GarbageGraspResult.Result.PLAN_FAILED, 1);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.GRASP_FAILED) {
            return new GarbageGraspResult(GarbageGraspResult.Result.NAV_FAILED, 2);
        } else {
            //TODO add unknown state
            return new GarbageGraspResult(GarbageGraspResult.Result.PLAN_FAILED, 1);
        }
    }
}
