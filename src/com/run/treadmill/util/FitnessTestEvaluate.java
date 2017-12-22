package com.run.treadmill.util;

public class FitnessTestEvaluate {

	public static String getEvaluate( int mSex, int mAge, float score) {
		String evaluate = "Very Poor";
		switch (mSex) {
			case CTConstant.GENDER_GIRL:
				if( mAge >= 13 && mAge <= 19 ) {
					if( score < 25.0 ) {
						evaluate = "Very Poor";
					} else if( score >= 25.0 && score <= 30.9 ) {
						evaluate = "Poor";
					} else if( score >= 31.0 && score <= 34.9 ) {
						evaluate = "Fair";
					} else if( score >= 35.0 && score <= 38.9 ) {
						evaluate = "Good";
					} else if( score >= 39.0 && score <= 41.9 ) {
						evaluate = "Excellent";
					} else if( score > 41.9 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 20 && mAge <= 29 ) {
					if( score < 23.6 ) {
						evaluate = "Very Poor";
					} else if( score >= 23.6 && score <= 28.9 ) {
						evaluate = "Poor";
					} else if( score >= 29.0 && score <= 32.9 ) {
						evaluate = "Fair";
					} else if( score >= 33.0 && score <= 36.9 ) {
						evaluate = "Good";
					} else if( score >= 37.0 && score <= 41.0 ) {
						evaluate = "Excellent";
					} else if( score > 41.0 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 30 && mAge <= 39 ) {
					if( score < 22.8 ) {
						evaluate = "Very Poor";
					} else if( score >= 22.8 && score <= 26.9 ) {
						evaluate = "Poor";
					} else if( score >= 27.0 && score <= 31.4 ) {
						evaluate = "Fair";
					} else if( score >= 31.5 && score <= 35.6 ) {
						evaluate = "Good";
					} else if( score >= 35.7 && score <= 40.0 ) {
						evaluate = "Excellent";
					} else if( score > 40.0 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 40 && mAge <= 49 ) {
					if( score < 21.0 ) {
						evaluate = "Very Poor";
					} else if( score >= 21.0 && score <= 24.4 ) {
						evaluate = "Poor";
					} else if( score >= 24.5 && score <= 28.9 ) {
						evaluate = "Fair";
					} else if( score >= 29.0 && score <= 32.8 ) {
						evaluate = "Good";
					} else if( score >= 32.9 && score <= 36.9 ) {
						evaluate = "Excellent";
					} else if( score > 36.9 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 50 && mAge <= 59 ) {
					if( score < 20.2 ) {
						evaluate = "Very Poor";
					} else if( score >= 20.2 && score <= 22.7 ) {
						evaluate = "Poor";
					} else if( score >= 22.8 && score <= 26.9 ) {
						evaluate = "Fair";
					} else if( score >= 27.0 && score <= 31.4 ) {
						evaluate = "Good";
					} else if( score >= 31.5 && score <= 35.7 ) {
						evaluate = "Excellent";
					} else if( score > 35.7 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 60 ) {
					if( score < 17.5 ) {
						evaluate = "Very Poor";
					} else if( score >= 17.5 && score <= 20.1 ) {
						evaluate = "Poor";
					} else if( score >= 20.2 && score <= 24.4 ) {
						evaluate = "Fair";
					} else if( score >= 24.5 && score <= 30.2 ) {
						evaluate = "Good";
					} else if( score >= 30.3 && score <= 31.4 ) {
						evaluate = "Excellent";
					} else if( score > 31.4 ) {
						evaluate = "Supenor";
					}
				}
				break;



			case CTConstant.GENDER_BOY:
				if( mAge >= 13 && mAge <= 19 ) {
					if( score < 35.0 ) {
						evaluate = "Very Poor";
					} else if( score >= 35.0 && score <= 38.3 ) {
						evaluate = "Poor";
					} else if( score >= 38.4 && score <= 45.1 ) {
						evaluate = "Fair";
					} else if( score >= 45.2 && score <= 50.9 ) {
						evaluate = "Good";
					} else if( score >= 51.0 && score <= 55.9 ) {
						evaluate = "Excellent";
					} else if( score > 55.9 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 20 && mAge <= 29 ) {
					if( score < 33.0 ) {
						evaluate = "Very Poor";
					} else if( score >= 33.0 && score <= 36.4 ) {
						evaluate = "Poor";
					} else if( score >= 36.5 && score <= 43.4 ) {
						evaluate = "Fair";
					} else if( score >= 42.5 && score <= 46.4 ) {
						evaluate = "Good";
					} else if( score >= 46.5 && score <= 52.4 ) {
						evaluate = "Excellent";
					} else if( score > 52.4 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 30 && mAge <= 39 ) {
					if( score < 31.5 ) {
						evaluate = "Very Poor";
					} else if( score >= 31.5 && score <= 35.4 ) {
						evaluate = "Poor";
					} else if( score >= 35.5 && score <= 40.9 ) {
						evaluate = "Fair";
					} else if( score >= 41.0 && score <= 44.9 ) {
						evaluate = "Good";
					} else if( score >= 45.0 && score <= 49.4 ) {
						evaluate = "Excellent";
					} else if( score > 49.4 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 40 && mAge <= 49 ) {
					if( score < 30.2 ) {
						evaluate = "Very Poor";
					} else if( score >= 30.2 && score <= 33.5 ) {
						evaluate = "Poor";
					} else if( score >= 33.6 && score <= 38.9 ) {
						evaluate = "Fair";
					} else if( score >= 39.0 && score <= 43.7 ) {
						evaluate = "Good";
					} else if( score >= 43.8 && score <= 48.0 ) {
						evaluate = "Excellent";
					} else if( score > 48.0 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 50 && mAge <= 59 ) {
					if( score < 26.1 ) {
						evaluate = "Very Poor";
					} else if( score >= 26.1 && score <= 30.9 ) {
						evaluate = "Poor";
					} else if( score >= 31.0 && score <= 35.7 ) {
						evaluate = "Fair";
					} else if( score >= 35.8 && score <= 40.9 ) {
						evaluate = "Good";
					} else if( score >= 41.0 && score <= 45.3 ) {
						evaluate = "Excellent";
					} else if( score > 45.3 ) {
						evaluate = "Supenor";
					}
				} else if( mAge >= 60 ) {
					if( score < 20.5 ) {
						evaluate = "Very Poor";
					} else if( score >= 20.5 && score <= 26.0 ) {
						evaluate = "Poor";
					} else if( score >= 26.1 && score <= 32.2 ) {
						evaluate = "Fair";
					} else if( score >= 32.3 && score <= 36.4 ) {
						evaluate = "Good";
					} else if( score >= 36.5 && score <= 44.2 ) {
						evaluate = "Excellent";
					} else if( score > 44.2 ) {
						evaluate = "Supenor";
					}
				}
				break;
			default:
				break;
		}

		return evaluate;
	}

}
