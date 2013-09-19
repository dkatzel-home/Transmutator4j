/*******************************************************************************
 * Copyright (c) 2010 - 2013 Danny Katzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package net.transmutator4j.ant;

import java.io.File;
import java.util.Arrays;



import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * {@code TransmorgifyTask} is an ant task
 * implementation that lets clients invoke
 * Transmorgify from an ant script.
 * 
 * Here is how to invoke Transmorgify from ant
 * <pre>
 * 
 &lt;taskdef name="transmorgify" classname="net.transmutator4j.ant.TransmorgifyTask" classpathref="test.classpath"/&gt;
		
 &lt;target name = "mutation-tests"&gt;
		&lt;transmorgify
			classpathRef="test.classpath"
			mutateclasses="my.project.Foo*"
			outputfile="transmorgify.out.xml"
			testsuite="my.project.TestFoo"
			
		/&gt;
&lt;/target&gt;
</pre>
 * @author dkatzel
 *
 */
public class TransmorgifyTask extends Task{

	
	/**
	 * Command line used to invoke java
	 * (this is how the ant java task does it).
	 */
	private Path classpath;
    private File out;
    private String classesToMutatePattern;
    private String testSuite;
    private  Reference classpathRef;
   

	public void setTestSuite(String testSuite) {
		this.testSuite = testSuite;
	}


	public void setMutateClasses(String mutateClasses) {
		this.classesToMutatePattern = mutateClasses;
	}


	public void setOutputFile(File outputFile) {
		out = outputFile;
	}


    /**
     * Set the classpath to be used when running the Java class.
     *
     * @param s an Ant Path object containing the classpath.
     */
    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public void setClasspathRef(Reference r) {
    	classpathRef = r;
    }
    /**
     * Add a path to the classpath.
     *
     * @return created classpath.
     */
    public Path createClasspath() {
        if(classpath ==null){
        	classpath = new Path(this.getProject());
        }
        return classpath.createPath();
    }


    
	@Override
	public void execute() throws BuildException {
		System.out.println("classpathRef = " + classpathRef);
		Java javaTask = (Java)getProject().createTask("java");
		javaTask.setTaskName(getTaskName());
		javaTask.setClassname("net.transmutator4j.RunTransmorgify");
		if(classpath !=null){
			javaTask.setClasspath(classpath);
		}
		if(classpathRef !=null){
			javaTask.setClasspathRef(classpathRef);
		}
		
		javaTask.createArg().setValue("-test");
		javaTask.createArg().setValue(testSuite);
		
		javaTask.createArg().setValue("-classes");
		javaTask.createArg().setValue(classesToMutatePattern);
		
		javaTask.createArg().setValue("-out");
		javaTask.createArg().setFile(out);
		
		
		javaTask.setFork(true);
		System.out.println(Arrays.toString(javaTask.getCommandLine().getCommandline()));
		if(javaTask.executeJava() !=0){
			throw new BuildException("error");
		}
	}   
}
