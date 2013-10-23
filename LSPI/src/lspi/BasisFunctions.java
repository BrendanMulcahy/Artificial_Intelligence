package lspi;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Jama.Matrix;

public abstract class BasisFunctions {
	private ArrayList<Method> basisFunctions = new ArrayList<Method>();
	
	/**
	 * Constructor of Basis Functions
	 * Gets all the methods with the "BasisFunction" annotation 
	 * Extending classes' constructor should super() this.
	 */
	public BasisFunctions() {		
		Method[] allMethods = this.getClass().getDeclaredMethods();
		Map<Integer, Method> map = new HashMap<Integer, Method>();
		MethodLoop:
		for(Method method : allMethods) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for(Annotation annotation : annotations) {
				if (annotation instanceof BasisFunction) {
					map.put(((BasisFunction) annotation).value(), method);
					//basisFunctions.add(method);
					continue MethodLoop;
				}
			}
		}
		
		//Put methods into the basis functions list sorted
		for(int i = 0; i < map.keySet().size(); i++) {
			Method m = map.get(i);
			System.out.println("Found " + i + ":" + m.getName());
			basisFunctions.add(m);
		}
	}
	
	/**
	 * Evaluates all the basis functions on the given input
	 * and then returns a column matrix containing the values
	 * @param s the current state
	 * @param a the action being considered
	 * @return column matrix containing the basis function values
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Matrix evaluate(State s, Action a) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Matrix phi = new Matrix(basisFunctions.size(), 1); //k x 1 vector
		int count = 0;
		for(Method method : basisFunctions) {
			Double value = (Double) method.invoke(this, s, a);
			phi.set(count, 0, value);
			count++;
		}
		
		return phi;
	}
}
