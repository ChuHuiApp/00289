package com.run.treadmill.runningParamManager;


public interface ParamInterface {
	public String TAG = "ParamInterface";

	public boolean isAccThirSec();

	public boolean isAccElevenSec();

	public boolean isAccOneMin();

	public boolean isRunEnd();

	public void getRunStageNum();

	public boolean isShowLevelIcon();

	public float getShowRunTime();

}
