package uk.ac.ebi.cyrface2.internal.examples.dataRail.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cyrface2.internal.CyActivator;
import uk.ac.ebi.cyrface2.internal.examples.dataRail.DataRailAttributes;
import uk.ac.ebi.cyrface2.internal.examples.dataRail.DataRailModel;
import uk.ac.ebi.cyrface2.internal.examples.dataRail.tasks.NormaliseCnoListTask;
import uk.ac.ebi.cyrface2.internal.examples.dataRail.tasks.OptimiseCnoListTask;
import uk.ac.ebi.cyrface2.internal.utils.PlotsDialog;

public class ContextMenuFactory implements CyNodeViewContextMenuFactory {
	
	private CyActivator activator;	
	private DataRailModel model;
	
	private CyNetworkView view;
	private CyNetwork network;
	private CyTable defaultNodeTable;
	
	private List<Long> workflowNodesSUIDs;
	
	
	public ContextMenuFactory (CyActivator activator, List<Long> workflowNodesSUIDs, DataRailModel model) {
		this.activator = activator;
		this.model = model;
		this.workflowNodesSUIDs = workflowNodesSUIDs;
	}

	public CyMenuItem createMenuItem (CyNetworkView netView, View<CyNode> nodeView) {
		
		view = activator.cyApplicationManager.getCurrentNetworkView();
		network = activator.cyApplicationManager.getCurrentNetwork();
		defaultNodeTable = network.getDefaultNodeTable();
		
		final long nodeSUID = nodeView.getModel().getSUID();
		CyMenuItem menuItem;
		
		switch (workflowNodesSUIDs.indexOf(nodeSUID)) {
			case 0: 
				menuItem = createBrowseMidasMenu(nodeSUID); break;
			case 1: 
				menuItem = createLoadMidasMenu(nodeSUID); break;
			case 2: 
				menuItem = createPlotCNOListMenu(nodeSUID); break;
			case 3: 
				menuItem = createNormaliseMenu(nodeSUID); break;
			case 4: 
				menuItem = createPlotNormalisedCNOListMenu(nodeSUID); break;
			case 5: 
				menuItem = createOptimiseMenu(nodeSUID); break;
			case 6: 
				menuItem = createOptimisedCNOListMenu(nodeSUID); break;
			default: 
				menuItem = null; break;
		}
		
		return menuItem;
	}
	
	/**
	 * Set Midas File node context menu
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createBrowseMidasMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.SET_MIDAS_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseMidasMenuFunction(nodeSUID);
			}
		});
		menuItem.setToolTipText(DataRailAttributes.SET_MIDAS_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}
	
	/**
	 * Set Midas File context menu function
	 * 
	 * @param nodeSUID
	 */
	private void browseMidasMenuFunction (final long nodeSUID) {
		JFileChooser fc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("csv-files", "csv");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		
		int browserReturn = fc.showOpenDialog(null);
		
		if (browserReturn == JFileChooser.APPROVE_OPTION){
			String midasFilePath = fc.getSelectedFile().getAbsolutePath();
			model.setMidasFilePath(midasFilePath);
			
			network.getDefaultNodeTable().getRow(nodeSUID).set(DataRailAttributes.NODE_STATUS, DataRailAttributes.NODE_STATUS_DEFINED);
			view.updateView();
		}
	}
	
	/**
	 * Load Midas context menu
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createLoadMidasMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.LOAD_MIDAS_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMidasFunction(nodeSUID);
			}
		});
		
		menuItem.setToolTipText(DataRailAttributes.LOAD_MIDAS_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}
	
	/**
	 * Load Midas menu function
	 * 
	 * @param nodeSUID
	 */
	private void loadMidasFunction (final long nodeSUID) {
		try {
			CyRow step1Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(0));
			boolean isPreviousStepDefined = step1Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);
			
			if (isPreviousStepDefined) {
				model.getRCommand().loadMidasFile(model.getMidasFilePath());

				network.getDefaultNodeTable().getRow(nodeSUID).set(DataRailAttributes.NODE_STATUS, DataRailAttributes.NODE_STATUS_DEFINED);
				network.getDefaultNodeTable().getRow(workflowNodesSUIDs.get(2)).set(DataRailAttributes.NODE_STATUS, DataRailAttributes.NODE_STATUS_DEFINED);
				view.updateView();

			}else{
				JOptionPane.showMessageDialog(null, DataRailAttributes.LOAD_MIDAS_ERROR_MESSAGE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * CNO list Context Menu 
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createPlotCNOListMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.CNO_LIST_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotCNOListFunction(nodeSUID);
			}
		});
		menuItem.setToolTipText(DataRailAttributes.CNO_LIST_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}

	/** CNO List menu function
	 * 
	 * @param nodeSUID
	 */
	private void plotCNOListFunction (final long nodeSUID) {
		try {
			CyRow step2Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(1));
			boolean isPreviousStepDefined = step2Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);

			if (isPreviousStepDefined) {
				File cnoListPlot = model.getRCommand().plotCnoList(RFunctionsModel.VAR_CNO_LIST);
				model.setCnoListPlot(cnoListPlot);

				PlotsDialog panel = new PlotsDialog(cnoListPlot);
				panel.display();

				network.getDefaultNodeTable().getRow(nodeSUID).set(DataRailAttributes.NODE_STATUS, DataRailAttributes.NODE_STATUS_DEFINED);
				view.updateView();

			}else{
				JOptionPane.showMessageDialog(null, DataRailAttributes.CNO_LIST_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Normalise CNO list context menu
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createNormaliseMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.NORMALISE_CNO_CONTEXT_MENU_NAME);
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyRow step3Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(2));
				boolean isPreviousStepDefined = step3Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);
				
				if (isPreviousStepDefined) {
					activator.dialogTaskManager.execute(new TaskIterator(new NormaliseCnoListTask(model, workflowNodesSUIDs, defaultNodeTable, network, view)));
				}
			}
		});
		
		menuItem.setToolTipText(DataRailAttributes.NORMALISE_CNO_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}
	
	
	/**
	 * CNO List Normalised context menu
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createPlotNormalisedCNOListMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.NORMALISED_CNO_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotNormalisedCNOList (nodeSUID);
			}
		});
		menuItem.setToolTipText(DataRailAttributes.NORMALISED_CNO_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}
	
	/**
	 * CNO List Normalized menu function
	 * 
	 * @param nodeSUID
	 */
	private void plotNormalisedCNOList (final long nodeSUID) {
		try {
			CyRow step4Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(3));
			boolean isPreviousStepDefined = step4Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);

			if (isPreviousStepDefined) {
				File cnoListPlot = model.getRCommand().plotCnoList(RFunctionsModel.VAR_NORM_CNO_LIST);
				model.setCnoListPlot(cnoListPlot);

				PlotsDialog panel = new PlotsDialog(cnoListPlot);
				panel.display();
			}else{
				JOptionPane.showMessageDialog(null, DataRailAttributes.CNO_LIST_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Optimize CNO
	 * 
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createOptimiseMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.OPTMISE_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				optimiseFunction(nodeSUID);
			}
		});
		menuItem.setToolTipText(DataRailAttributes.OPTMISE_CONTEXT_MENU_TOOL_TIP);
		return new CyMenuItem(menuItem, 0);
	}
	
	/**
	 * Optimize menu function
	 * 
	 * @param nodeSUID
	 */
	public void optimiseFunction (final long nodeSUID) {
		CyRow step5Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(4));
		boolean isPreviousStepDefined = step5Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);

		if (isPreviousStepDefined) {
			JFileChooser fc = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("sif or sbml", "sif", "sbml", "xml");
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
			
			int browserReturn = fc.showOpenDialog(null);
			
			if (browserReturn == JFileChooser.APPROVE_OPTION){
				model.setPknModelFile(fc.getSelectedFile().getAbsolutePath());
				activator.dialogTaskManager.execute(new TaskIterator(new OptimiseCnoListTask(model, workflowNodesSUIDs, defaultNodeTable, network, view)));
			}
		}
	}
	
	/**
	 * Plot Optmised CNO list menu
	 * @param nodeSUID
	 * @return
	 */
	private CyMenuItem createOptimisedCNOListMenu (final long nodeSUID) {
		JMenuItem menuItem = new JMenuItem(DataRailAttributes.PLOT_OPTIMISED_CONTEXT_MENU_NAME);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotOptimisedFunction (nodeSUID);
				
			}
		});
		menuItem.setToolTipText(DataRailAttributes.PLOT_OPMITSED_CONTEXT_MENU_TOOL_TIP); 
		return new CyMenuItem(menuItem, 0);
	}
	
	/**
	 * Plot Optmised CNO list menu function
	 * 
	 * @param nodeSUID
	 */
	private void plotOptimisedFunction (final long nodeSUID) {
		try {
			CyRow step6Row = defaultNodeTable.getRow(workflowNodesSUIDs.get(5));
			boolean isPreviousStepDefined = step6Row.get(DataRailAttributes.NODE_STATUS, String.class).equals(DataRailAttributes.NODE_STATUS_DEFINED);

			if (isPreviousStepDefined) {
				File cnoListPlot = model.getRCommand().cutAndPlot();
				model.setCnoListOptimisedPlot(cnoListPlot);

				PlotsDialog panel = new PlotsDialog(cnoListPlot);
				panel.display();
			} else {
				JOptionPane.showMessageDialog(null, DataRailAttributes.CNO_LIST_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
