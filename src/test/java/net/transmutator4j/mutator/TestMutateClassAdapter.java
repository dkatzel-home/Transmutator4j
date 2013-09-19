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
package net.transmutator4j.mutator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.transmutator4j.mutations.Mutation;
import net.transmutator4j.mutator.AbstractTestMutateMethodAdapter.MockMethodVisitor;
import net.transmutator4j.util.TransmorgifyUtil;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestMutateClassAdapter {

	private MutateClassAdapter sut;
	private ClassVisitorTestDouble visitorTestDouble;
	private final int version= 160;
	private final int access = 1234;
	private final String name = "org/some/java/class";
	private final String desc = "description";
	
	private final String signature = "signature";
	private final String[] exceptions = new String[]{"excpetion_1","exception_2"};
	
	private final String superName = "superName";
	private final String[] interfaces = new String[]{"interface_1","interface_2"};
	@Before
	public void setup(){
		visitorTestDouble = new ClassVisitorTestDouble();
	}
	
	@Test
	public void shouldMutate(){
		sut = new MutateClassAdapter(visitorTestDouble, 0);
		assertTrue(sut.shouldMutate());
	}
	@Test
	public void shouldNotMutate(){
		sut = new MutateClassAdapter(visitorTestDouble, 1);
		assertFalse(sut.shouldMutate());
	}
	@Test
	public void keepIncrementingUntilCanMutate(){
		sut = new MutateClassAdapter(visitorTestDouble, 10);
		for(int i=0; i< 10; i++){
			assertFalse(sut.shouldMutate());
		}
		assertTrue(sut.shouldMutate());
	}
	
	@Test
	public void visitMethodParentReturnsNullShouldReturnNull(){
		visitorTestDouble.expectVisitMethod(access, name, desc, signature, exceptions, null);
		sut = new MutateClassAdapter(visitorTestDouble,0);
		assertNull(sut.visitMethod(access, name, desc, signature, exceptions));
		visitorTestDouble.verifyAllExpectationsMet();
	}
	
	@Test
	public void visitMethodAndHasntMutatedShouldReturnMutateMethodAdapter(){
		MockMethodVisitor mockMethodVisitor = new MockMethodVisitor();
		visitorTestDouble.expectVisitMethod(access, name, desc, signature, exceptions,mockMethodVisitor);
		sut = new MutateClassAdapter(visitorTestDouble,0);
		MethodVisitor expectedMethodVisitor = sut.visitMethod(access, name, desc, signature, exceptions);
		assertNotNull(expectedMethodVisitor);
		assertNotSame(mockMethodVisitor, expectedMethodVisitor);
		assertTrue(expectedMethodVisitor instanceof MutateMethodAdapter);
		visitorTestDouble.verifyAllExpectationsMet();
	}
	
	@Test
	public void visitClassShouldDelegateToParent(){
		visitorTestDouble.expectVisit(version, access, name, signature, superName, interfaces);
		sut = new MutateClassAdapter(visitorTestDouble,0);
		sut.visit(version, access, name, signature, superName, interfaces);
		visitorTestDouble.verifyAllExpectationsMet();
	}
	
	@Test
	public void mutate(){
		int lineNumber = 9876;
		visitorTestDouble.expectVisit(version, access, name, signature, superName, interfaces);
		sut = new MutateClassAdapter(visitorTestDouble,0);
		Mutation mutation = createMock(Mutation.class);		
		sut.visit(version, access, name, signature, superName, interfaces);
		sut.mutate(lineNumber, mutation);
		assertEquals(mutation,sut.getMutation());
		assertEquals(name.replaceAll("/", "\\."), sut.getMutatedClassname());
		assertEquals(lineNumber, sut.getMutatedLine());
		visitorTestDouble.verifyAllExpectationsMet();
	}
	@Test
	public void mutateASecondTimeShouldThrowIllegalStateException(){
		int lineNumber = 9876;
		visitorTestDouble.expectVisit(version, access, name, signature, superName, interfaces);
		sut = new MutateClassAdapter(visitorTestDouble,0);
		Mutation mutation = createMock(Mutation.class);		
		sut.visit(version, access, name, signature, superName, interfaces);
		sut.mutate(lineNumber, mutation);
		try{
		sut.mutate(lineNumber, mutation);
		fail("should throw IllegalStateException");
		}catch(IllegalStateException e){
			assertEquals("already mutated!", e.getMessage());
		}
		visitorTestDouble.verifyAllExpectationsMet();
	}
	
	public static class ClassVisitorTestDouble extends ClassVisitor{
		private final List<VisitExpectation> expectedVisits = new ArrayList<>();
		
		private final Map<VisitMethodExpectation, MethodVisitor> expectedMethodVisits = new HashMap<>();
		public ClassVisitorTestDouble() {
			super(TransmorgifyUtil.CURRENT_ASM_VERSION);
		}

		public void expectVisit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			expectedVisits.add(new VisitExpectation(version, access, name, signature, superName, interfaces));
		}
		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			VisitExpectation expected =new VisitExpectation(version, access, name, signature, superName, interfaces);
		
			if(!expectedVisits.contains(expected)){
				throw new AssertionError("unexpected visit : "+ expected);
			}
			expectedVisits.remove(expected);
			super.visit(version, access, name, signature, superName, interfaces);
		}
		
		
		public void expectVisitMethod(int access, String name, String desc,
				String signature, String[] exceptions, MethodVisitor returnVisitor) {
			VisitMethodExpectation expected = new VisitMethodExpectation(access, name, desc, signature, exceptions);
			expectedMethodVisits.put(expected, returnVisitor);
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			VisitMethodExpectation expected = new VisitMethodExpectation(access, name, desc, signature, exceptions);
			
			if(!expectedMethodVisits.containsKey(expected)){
				throw new AssertionError("unexpected visit method : "+ expected);
			}
			return expectedMethodVisits.remove(expected);
		}

		public void verifyAllExpectationsMet(){
			if(!expectedVisits.isEmpty()){
				throw new AssertionError("did not visit expectations : "+ expectedVisits);
			}
			if(!expectedMethodVisits.isEmpty()){
				throw new AssertionError("did not visit method expectations : "+ expectedMethodVisits);
			}
		}
		
		private static class VisitMethodExpectation{
			private final int access;
			private final String name, desc, signature;
			private final String[] exceptions;
			public VisitMethodExpectation(int access, String name, String desc,
					String signature, String[] exceptions) {
				this.access = access;
				this.name = name;
				this.desc = desc;
				this.signature = signature;
				this.exceptions = exceptions;
			}
			@Override
			public String toString() {
				return "VisitMethodExpectation [access=" + access + ", name="
						+ name + ", desc=" + desc + ", signature=" + signature
						+ ", exceptions=" + Arrays.toString(exceptions) + "]";
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + access;
				result = prime * result
						+ ((desc == null) ? 0 : desc.hashCode());
				result = prime * result + Arrays.hashCode(exceptions);
				result = prime * result
						+ ((name == null) ? 0 : name.hashCode());
				result = prime * result
						+ ((signature == null) ? 0 : signature.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				VisitMethodExpectation other = (VisitMethodExpectation) obj;
				if (access != other.access)
					return false;
				if (desc == null) {
					if (other.desc != null)
						return false;
				} else if (!desc.equals(other.desc))
					return false;
				if (!Arrays.equals(exceptions, other.exceptions))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (signature == null) {
					if (other.signature != null)
						return false;
				} else if (!signature.equals(other.signature))
					return false;
				return true;
			}
			
			
		}
		
		private static class VisitExpectation{
			private final int version, access;
			private final String name, signature, superName;
			private final String[] interfaces;
			
			public VisitExpectation(int version, int access, String name,
					String signature, String superName, String[] interfaces) {
				this.version = version;
				this.access = access;
				this.name = name;
				this.signature = signature;
				this.superName = superName;
				this.interfaces = interfaces;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + access;
				result = prime * result + Arrays.hashCode(interfaces);
				result = prime * result
						+ ((name == null) ? 0 : name.hashCode());
				result = prime * result
						+ ((signature == null) ? 0 : signature.hashCode());
				result = prime * result
						+ ((superName == null) ? 0 : superName.hashCode());
				result = prime * result + version;
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				VisitExpectation other = (VisitExpectation) obj;
				if (access != other.access)
					return false;
				if (!Arrays.equals(interfaces, other.interfaces))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (signature == null) {
					if (other.signature != null)
						return false;
				} else if (!signature.equals(other.signature))
					return false;
				if (superName == null) {
					if (other.superName != null)
						return false;
				} else if (!superName.equals(other.superName))
					return false;
				if (version != other.version)
					return false;
				return true;
			}

			@Override
			public String toString() {
				return "VisitExpectation [version=" + version + ", access="
						+ access + ", name=" + name + ", signature="
						+ signature + ", superName=" + superName
						+ ", interfaces=" + Arrays.toString(interfaces) + "]";
			}
			
			
		}
		
	}
}
