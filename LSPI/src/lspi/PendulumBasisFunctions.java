package lspi;

public class PendulumBasisFunctions extends BasisFunctions {
	public PendulumBasisFunctions() {
		super();
	}
	
	//10 Basis Functions for LeftOperation
	@BasisFunction(0)
	public double LeftConstant(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@BasisFunction(1)
	public double Left_11(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(2)
	public double Left_12(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(3)
	public double Left_13(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(4)
	public double Left_21(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(5)
	public double Left_22(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(6)
	public double Left_23(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(7)
	public double Left_31(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(8)
	public double Left_32(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(9)
	public double Left_33(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Left) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	//10 Basis Functions for NoOperation
	@BasisFunction(10)
	public double NoneConstant(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@BasisFunction(11)
	public double None_11(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(12)
	public double None_12(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(13)
	public double None_13(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(14)
	public double None_21(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(15)
	public double None_22(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(16)
	public double None_23(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(17)
	public double None_31(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(18)
	public double None_32(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(19)
	public double None_33(PendulumState state, PendulumAction action) {
		if (action instanceof Force_None) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	//10 Basis Functions for RightOperation
	@BasisFunction(20)
	public double RightConstant(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@BasisFunction(21)
	public double Right_11(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(22)
	public double Right_12(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(23)
	public double Right_13(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(-Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(24)
	public double Right_21(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(25)
	public double Right_22(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(26)
	public double Right_23(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(0 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(27)
	public double Right_31(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(-Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(28)
	public double Right_32(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(0 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
	@BasisFunction(29)
	public double Right_33(PendulumState state, PendulumAction action) {
		if (action instanceof Force_Right) {
			return Math.exp(-0.5 * Math.pow(Math.pow(+Math.PI/4 - state.x, 2) + Math.pow(+Math.PI/4 - state.v, 2), 0.5));
		} else {
			return 0;
		}
	}
	
}
