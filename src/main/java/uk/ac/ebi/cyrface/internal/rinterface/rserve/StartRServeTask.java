package uk.ac.ebi.cyrface.internal.rinterface.rserve;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

public class StartRServeTask extends AbstractTask implements ObservableTask {

	public StartRServeTask() { }
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Configuring Cyrface");

		taskMonitor.setProgress(0.0);
		taskMonitor.setStatusMessage("Starting Rserve...");
		
		String osName = System.getProperty("os.name").toLowerCase();
		taskMonitor.setStatusMessage(osName);
		
		if (osName.indexOf("win") >= 0) {			
			launchRserveWindows(taskMonitor);
	
		} else if (osName.indexOf("mac") >= 0) {
			launchRserveUnix(taskMonitor);
	
		} else if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 ) {
			launchRserveUnix(taskMonitor);
			
		} else {
			throw new Exception ("Operating system not supported: " + osName);
		}
		
		taskMonitor.setStatusMessage("Cyrface ready!");
	}

	private void launchRserveWindows (TaskMonitor taskMonitor) throws Exception {		
		String rPath = "";

		// check the windows registry for an "R"-entry that proofs that and where R is installed
		Process proc = Runtime.getRuntime().exec("reg query HKLM\\Software\\R-core\\R");

		// save all information from the R-registry-entry
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		// wait till the process has terminated
		proc.waitFor();

		StringBuffer output = new StringBuffer();

		String line = "";		

		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");

			// get the R-install-path reading the information of the registry-entry
			if(line.contains("InstallPath") && line.contains("REG_SZ")) {

				// erase unimportant information from the line and correct the path
				rPath = line.substring(line.indexOf(":\\")-1).trim().replace("\\", "/");

				// extend the path of the R-folder to the location of the R.exe-file
				rPath = "\""+rPath+"/bin/R.exe\"";
			}
		}

		// Check if R is installed
		if (!rExeExist_win(rPath)) throw new Exception("R is not installed");

		// Check is Rserve is installed
		if (!rserveIsInstalled_win(rPath)) installRserve_win(rPath);

		// Check if Rserve is running
		startRserve_win(rPath);

	}

	private void launchRserveUnix (TaskMonitor taskMonitor) throws Exception {

		// Check if R is installed
		if (!rIsInstalled_unix()) throw new Exception("R is not installed");
		
		taskMonitor.setStatusMessage("R installed, good!");

		// Check is Rserve is installed
		if (!rserveIsInstalled_unix()) installRserve_unix();
		
		taskMonitor.setStatusMessage("RServe installed, good!");

		// Check is Rserve is running
		startRserve_unix();
		
		taskMonitor.setStatusMessage("RServe installed and running, perfect!");
	}

	/**
	 * This method checks, if the Rserve.exe-file exists in the given path.
	 */
	private boolean rExeExist_win (String rPath) throws Exception {
		File rserve = new File(rPath.replace("\"", ""));		
		return rserve.exists();	
	}

	/**
	 * This method checks in windows if Rserve is installed returning true, otherwise returns false.
	 */
	private boolean rserveIsInstalled_win(String rPath) throws Exception {

		String rargs = "--no-save --slave";
		Process proc = Runtime.getRuntime().exec(rPath + " -e \"find.package('Rserve')\" " + rargs);

		if (proc.waitFor() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method installes Rserve for windows.
	 */
	private void installRserve_win(String rPath) throws Exception {		
		String rargs = "--no-save --slave";
		Process proc = Runtime.getRuntime().exec(rPath+" -e \"install.packages('Rserve'," + "repos='http://cran.us.r-project.org')\" " + rargs);
		proc.waitFor();			
	}

	/**
	 * This methods starts the Rserve.exe in windows.
	 */
	private void startRserve_win (String installPath) throws Exception {
		String rsrvargs = "--vanilla";
		String rargs = "--no-save --slave";

		Process proc = Runtime.getRuntime().exec(installPath+" -e \"library(Rserve)" + ";Rserve(FALSE,args='" + rsrvargs + "')\" " + rargs);
		proc.waitFor();
	}


	/**
	 * This method checks in an unix-based system, if R is installed.
	 */
	private boolean rIsInstalled_unix() throws Exception {
		Process proc = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "R --version"});

		if (proc.waitFor() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks in an unix-based system if Rserve is installed.
	 */
	private boolean rserveIsInstalled_unix() throws Exception {
		String rargs = "--no-save --slave";
		String cmd = getRInstallPath_unix();

		Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "echo 'find.package(\"Rserve\")'|" + cmd + " " + rargs});

		if (proc.waitFor() == 0)
			return true;
		else
			return false;
	}


	/**
	 * This method installs Rserve in an unix-based system.
	 */
	private boolean installRserve_unix() throws Exception {
		String rargs = "--no-save --slave";
		String cmd = getRInstallPath_unix();
		Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "echo 'install.packages(\"Rserve\", repos=\"http://cran.us.r-project.org\")'|" + cmd + " " + rargs});

		if (proc.waitFor() == 0)
			return true;
		else
			return false;
	}

	/**
	 * This method provides default installation-paths for R on an unix-based sytem.
	 * These paths will be used for checking, if R is installed on the computer.
	 */
	private String getRInstallPath_unix(){
		if((new File("/Library/Frameworks/R.framework/Resources/bin/R")).exists()){
			return"/Library/Frameworks/R.framework/Resources/bin/R";

		}else if((new File("/usr/local/lib/R/bin/R")).exists()){
			return "/usr/local/lib/R/bin/R";

		}else if((new File("/usr/lib/R/bin/R")).exists()){
			return "/usr/lib/R/bin/R";	

		}else if((new File("/usr/local/bin/R")).exists()){
			return "/usr/local/bin/R";

		}else if((new File("/sw/bin/R")).exists()){
			return "/sw/bin/R";

		}else if((new File("/usr/common/bin/R")).exists()){
			return "/usr/common/bin/R";

		}else if((new File("/opt/bin/R")).exists()){
			return "/opt/bin/R";

		}else if((new File("R")).exists()){
			return "R";
		}else{
			return null;
		}
	}

	/**
	 * This method starts Rserve in an unix-based system.
	 */
	private boolean startRserve_unix() throws Exception {
		String rsrvargs = "--vanilla --no-save"; // --no-save --slave
		String rargs = "--no-save --slave";
		String cmd = getRInstallPath_unix();

		if (cmd == null) throw new Exception("R install path not found");

		Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "echo 'library(Rserve); Rserve(args=\"" + rsrvargs + "\"" + ")'|" + cmd + " " + rargs});

		if (proc.waitFor() == 0)
			return true;
		else
			return false;
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		return null;
	}	
}
