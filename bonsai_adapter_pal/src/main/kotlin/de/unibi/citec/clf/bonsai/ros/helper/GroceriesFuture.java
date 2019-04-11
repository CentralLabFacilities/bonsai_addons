package de.unibi.citec.clf.bonsai.ros.helper;

import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.btl.util.StoringGroceriesResult;
import storing_groceries_msgs.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GroceriesFuture implements Future<StoringGroceriesResult> {

    ActionFuture<GroceriesActionGoal, GroceriesActionFeedback, GroceriesActionResult> action;

    public GroceriesFuture(ActionFuture<GroceriesActionGoal, GroceriesActionFeedback, GroceriesActionResult> f) {
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
    public StoringGroceriesResult get() throws InterruptedException, ExecutionException {
        return toStoringGroceriesResult(action.get());
    }

    @Override
    public StoringGroceriesResult get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return toStoringGroceriesResult(action.get(l,timeUnit));
    }

    private static StoringGroceriesResult toStoringGroceriesResult(GroceriesActionResult res) {

        if (res.getResult().getReturnCode().getCode() == returnCode.SUCCESS) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.SUCCESS, 0);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.PICK_FAILED) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.PICK_FAILED, 1);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.PLACE_FAILED) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.PLACE_FAILED, 2);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.NO_OBJECT_FOUND) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.NO_OBJECT_FOUND, 3);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.NO_PLACE_LOC_FOUND) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.NO_PLACE_LOC_FOUND, 4);
        } else if (res.getResult().getReturnCode().getCode() == returnCode.MOVE_FAILED) {
            return new StoringGroceriesResult(StoringGroceriesResult.Result.MOVE_FAILED, 5);
        } else {
            //TODO add unknown state
            return new StoringGroceriesResult(StoringGroceriesResult.Result.MOVE_FAILED, 5);
        }
    }
}
