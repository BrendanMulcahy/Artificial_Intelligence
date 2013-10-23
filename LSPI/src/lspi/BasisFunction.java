package lspi;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BasisFunction {
	//Just serves as a marker in the BasisFunctions class
	int value();
}
