package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;

import edu.cwru.eecs.rl.core.model.Model;
import edu.cwru.eecs.rl.core.model.RMaxQModel;

public class IMaxNode_RMaxQModel extends IMaxNodeDecorator {

	private static final long serialVersionUID = -3148440127380397115L;

	//private BiMap<State, Integer> m_allStateSet;	
	
	private RMaxQModel m_model;
	
	protected ValueType m_vType = ValueType.RMaxQModel;
	
	public IMaxNode_RMaxQModel(IMaxNodeInterface iMaxNode) {
		super(iMaxNode);
		m_model = new RMaxQModel(this);
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		if(m_vType.equals(vType)) {
			ps.println("\n;;" + m_vType.toString());
			m_model.printModel(ps, real);
		}
		else
			m_iMaxNode.printVF(ps, real, vType);
	}
	
	@Override
	public Model getModel() {
		return m_model;
	}

}
