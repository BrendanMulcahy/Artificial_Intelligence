package edu.cwru.eecs.rl.core.maxnode;

import edu.cwru.eecs.rl.core.model.BMaxQModel;
import edu.cwru.eecs.rl.core.model.Model;



public class IMaxNode_BMaxQModel extends IMaxNodeDecorator {

	private static final long serialVersionUID = -2166927484623826919L;
	
	private BMaxQModel m_model;
	
	public IMaxNode_BMaxQModel(IMaxNodeInterface iMaxNode) {
		super(iMaxNode);
		m_model = new BMaxQModel(this);
	}
	
	@Override
	public Model getModel() {
		return m_model;
	}
	
	
//	public State genNextState(State state, MaxNode maxNode) {
//		if(!m_primitiveModelDistributions.containsKey(maxNode))
//			m_primitiveModelDistributions.put(maxNode, new BMaxQPrimitiveModel(m_allStateSet));
//		return m_primitiveModelDistributions.get(maxNode).sampleNextState(state);
//	}
//	public double genNextReward(State state, MaxNode maxNode) {
//		if(!m_primitiveModelDistributions.containsKey(maxNode))
//			m_primitiveModelDistributions.put(maxNode, new BMaxQPrimitiveModel(m_allStateSet));
//		return m_primitiveModelDistributions.get(maxNode).getMeanReward(state);
//
//	}
//	public void updateModel(State state, MaxNode maxNode, double reward, State nextState){
//		if(!m_primitiveModelDistributions.containsKey(maxNode))
//			m_primitiveModelDistributions.put(maxNode, new BMaxQPrimitiveModel(m_allStateSet));
//		m_primitiveModelDistributions.get(maxNode).update(state, reward, nextState);
//	}
}





