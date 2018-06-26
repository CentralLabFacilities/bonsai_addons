package de.unibi.citec.clf.bonsai.rsb.util;



import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.rst.RstSerializer.DeserializationException;
import de.unibi.citec.clf.btl.rst.RstTypeFactory;

public class FutureRst<BtlType extends Type, RstType extends GeneratedMessage> implements Future<BtlType> {
	
	private Future<RstType> internal;
	private Class<BtlType> btlType;
	
	public FutureRst(Future<RstType> wrapped, Class<BtlType> btlType) {
		internal = wrapped;
		this.btlType = btlType;
	}

	@Override
	public boolean cancel(boolean arg0) {
		return internal.cancel(arg0);
	}

	@Override
	public BtlType get() throws InterruptedException, ExecutionException {
		RstType t = internal.get();
		BtlType b;
		try {
			b = RstTypeFactory.getInstance().createType(t, btlType);
		} catch (DeserializationException e) {
			throw new ExecutionException(e);
		}
		return b;
	}

	@Override
	public BtlType get(long arg0, TimeUnit arg1) throws InterruptedException,
			ExecutionException, TimeoutException {
		RstType t = internal.get(arg0, arg1);
		BtlType b;
		try {
			b = RstTypeFactory.getInstance().createType(t, btlType);
		} catch (DeserializationException e) {
			throw new ExecutionException(e);
		}
		return b;
	}

	@Override
	public boolean isCancelled() {
		return internal.isCancelled();
	}

	@Override
	public boolean isDone() {
		return internal.isDone();
	}

}
