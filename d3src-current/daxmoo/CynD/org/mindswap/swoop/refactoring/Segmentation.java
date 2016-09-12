package org.mindswap.swoop.refactoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.swoop.SwoopModel;
import org.mindswap.swoop.reasoner.PelletReasoner;
import org.mindswap.swoop.utils.owlapi.AxiomCollector;
import org.mindswap.swoop.utils.owlapi.CorrectedRDFRenderer;
import org.mindswap.swoop.utils.owlapi.OWLDescriptionFinder;
import org.mindswap.swoop.utils.owlapi.OWLOntBuilder;
import org.semanticweb.owl.impl.model.OWLDataFactoryImpl;
import org.semanticweb.owl.impl.model.OWLDataPropertyInstanceImpl;
import org.semanticweb.owl.impl.model.OWLDataPropertyRangeAxiomImpl;
import org.semanticweb.owl.impl.model.OWLFunctionalPropertyAxiomImpl;
import org.semanticweb.owl.impl.model.OWLIndividualTypeAssertionImpl;
import org.semanticweb.owl.impl.model.OWLInverseFunctionalPropertyAxiomImpl;
import org.semanticweb.owl.impl.model.OWLInversePropertyAxiomImpl;
import org.semanticweb.owl.impl.model.OWLObjectPropertyInstanceImpl;
import org.semanticweb.owl.impl.model.OWLObjectPropertyRangeAxiomImpl;
import org.semanticweb.owl.impl.model.OWLPropertyDomainAxiomImpl;
import org.semanticweb.owl.impl.model.OWLSubPropertyAxiomImpl;
import org.semanticweb.owl.impl.model.OWLSymmetricPropertyAxiomImpl;
import org.semanticweb.owl.impl.model.OWLTransitivePropertyAxiomImpl;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLCardinalityRestriction;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectQuantifiedRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddPropertyAxiom;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.RemoveClassAxiom;
import org.semanticweb.owl.model.change.RemovePropertyAxiom;
import org.semanticweb.owl.model.change.SetFunctional;
import org.semanticweb.owl.model.change.SetSymmetric;
import org.semanticweb.owl.model.change.SetTransitive;
import org.semanticweb.owl.model.helper.OWLBuilder;

public class Segmentation {
		protected SwoopModel swoopModel;
		protected boolean DEBUG;
		private OWLClass nothing;
		private OWLClass thing;
		private OWLOntology source;
		private int nlocalityChecks;
		private Map testedLocality;
		
		
		public Segmentation(OWLOntology source) throws URISyntaxException, OWLException {
			this.source = source;
			this.DEBUG = false;
			OWLDataFactory df = source.getOWLDataFactory();
			this.thing = df.getOWLThing();
			this.nothing = df.getOWLNothing();
			this.nlocalityChecks = 0;
			this.testedLocality = new HashMap();
		}
		
	
		//Takes an OWLDescription and a signature replaces by bottom the entities not in the signature
		public OWLDescription replaceBottom(OWLDescription desc, Set sig) throws OWLException, URISyntaxException{
			
			OWLDataFactory df = source.getOWLDataFactory();
			
			if(desc instanceof OWLClass){
				if(sig.contains(((OWLClass)desc)))
					return desc;
				else
					return this.nothing;
			}
			if(desc instanceof OWLObjectSomeRestriction || desc instanceof OWLDataSomeRestriction){
				if(sig.contains(((OWLRestriction)desc).getProperty())){
					if (desc instanceof OWLObjectSomeRestriction){
						OWLDescription res = df.getOWLObjectSomeRestriction((OWLObjectProperty)((OWLRestriction)desc).getProperty(), 
								replaceBottom(((OWLObjectSomeRestriction)desc).getDescription(), sig));
						return res;
					}
					return desc;
				}
				else
					return this.nothing;	
			}
			
			if(desc instanceof OWLObjectAllRestriction || desc instanceof OWLDataAllRestriction){
				if(sig.contains(((OWLRestriction)desc).getProperty())){
					if (desc instanceof OWLObjectAllRestriction){
						OWLDescription res = df.getOWLObjectAllRestriction((OWLObjectProperty)((OWLRestriction)desc).getProperty(), 
								replaceBottom(((OWLObjectAllRestriction)desc).getDescription(), sig));
						return res;
					}
					return desc;
				}
				else
					return this.thing;	
			}
			
			
			if (desc instanceof OWLCardinalityRestriction)
			{
				try {
					if(((OWLCardinalityRestriction)desc).isAtLeast() || ((OWLCardinalityRestriction)desc).isExactly())
						if(sig.contains(((OWLCardinalityRestriction)desc).getProperty()))
							return desc;
						else
							return this.nothing;
					else
						if(sig.contains(((OWLCardinalityRestriction)desc).getProperty()))
							return desc;
						else
							return this.thing;
						
				} catch (OWLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			if (desc instanceof OWLNot){
				OWLDescription not = df.getOWLNot(replaceBottom(
						((OWLNot)desc).getOperand(),sig));
				return not;
				//return(builder.complementOf(replaceBottom(((OWLNot)desc).getOperand(), sig)));
			}
			
			if (desc instanceof OWLAnd){
				Set operands = new HashSet();
				operands = ((OWLAnd)desc).getOperands();
				OWLDescription conjunction = df.getOWLAnd(replaceBottom(operands, sig));
                return conjunction;  			
			}
			
			if (desc instanceof OWLOr){
				Set operands = new HashSet();
				operands = ((OWLOr)desc).getOperands();
				OWLDescription disjunction = df.getOWLOr(replaceBottom(operands, sig));
                return disjunction;  			
			}
			
			return null;
			
		}
		
		public Set replaceBottom(Set s, Set sig) throws OWLException, URISyntaxException{
			Set result = new HashSet();
			Iterator iter = s.iterator();
			while(iter.hasNext()){
				OWLDescription desc = (OWLDescription)iter.next();
				result.add(replaceBottom(desc, sig));
			}
			return result;
		}
/*
		public OWLClassAxiom replaceBottom(OWLClassAxiom ax, Set sig) throws OWLException, URISyntaxException{
			return ax;
		}
*/		
		public OWLClassAxiom replaceBottom(OWLClassAxiom ax, Set sig) throws OWLException, URISyntaxException{
			OWLClassAxiom axiom = null;
			if (ax instanceof OWLSubClassAxiom){
				OWLDescription sup = replaceBottom(((OWLSubClassAxiom)ax).getSuperClass(), sig);
				OWLDescription sub = replaceBottom(((OWLSubClassAxiom)ax).getSubClass(), sig);
				axiom = source.getOWLDataFactory().getOWLSubClassAxiom(sub,sup);
			}
			if (ax instanceof OWLEquivalentClassesAxiom ){
				Set eqclasses = replaceBottom(((OWLEquivalentClassesAxiom)ax).getEquivalentClasses(), sig);
			    axiom = source.getOWLDataFactory().getOWLEquivalentClassesAxiom(eqclasses);
			}
			if (ax instanceof OWLDisjointClassesAxiom){
				Set disjointclasses = replaceBottom(((OWLDisjointClassesAxiom)ax).getDisjointClasses(), sig);
				axiom = source.getOWLDataFactory().getOWLDisjointClassesAxiom(disjointclasses);
			}
			return axiom;
			
		}
		
	
		/*
		public Set getAxioms(OWLEntity entity, OWLOntology ont) {
			Set axioms = new HashSet();
			Set ontSet = new HashSet();
			ontSet.add(ont);
			try {
				OWLDataFactory ontDF = ont.getOWLDataFactory();
				if (entity instanceof OWLClass) {
					OWLClass cla = (OWLClass) entity;
					// get superclasses
					Set sup = OWLDescriptionFinder.getSuperClasses(cla, ontSet);
					for (Iterator iter = sup.iterator(); iter.hasNext();) {
						OWLDescription supCla = (OWLDescription) iter.next();
						OWLSubClassAxiom subAxiom = ontDF.getOWLSubClassAxiom(cla, supCla);
						axioms.add(subAxiom);
					}
					// get equivalent classes
					Set equ = OWLDescriptionFinder.getEquivalentClasses(cla, ontSet);
					for (Iterator iter = equ.iterator(); iter.hasNext();) {
						OWLDescription equCla = (OWLDescription) iter.next();
						Set equSet = new HashSet();
						equSet.add(cla);
						equSet.add(equCla);
						OWLEquivalentClassesAxiom equAxiom = ontDF.getOWLEquivalentClassesAxiom(equSet);
						axioms.add(equAxiom);
					}
					// get disjoints
					Set disj = OWLDescriptionFinder.getDisjoints(cla, ontSet);
					for (Iterator iter = disj.iterator(); iter.hasNext();) {
						OWLDescription disjCla = (OWLDescription) iter.next();
						Set disjSet = new HashSet();
						disjSet.add(cla);
						disjSet.add(disjCla);
						OWLDisjointClassesAxiom disAxiom = ontDF.getOWLDisjointClassesAxiom(disjSet);
						axioms.add(disAxiom);
					}
					//?? enumerations - included in OWLDescriptionFinder.equivalentClasses
				}
				else if (entity instanceof OWLProperty) {
					OWLProperty prop = (OWLProperty) entity;
					
					// get domains
					Set domSet = prop.getDomains(ont);
					for (Iterator iter = domSet.iterator(); iter.hasNext();) {
						OWLDescription domDesc = (OWLDescription) iter.next();
						OWLPropertyDomainAxiomImpl opda = new OWLPropertyDomainAxiomImpl((OWLDataFactoryImpl) ontDF, prop, domDesc);
						axioms.add(opda);
					}
					// get functional
					if (prop.isFunctional(ont)) {
						OWLFunctionalPropertyAxiomImpl ofp = new OWLFunctionalPropertyAxiomImpl((OWLDataFactoryImpl) ontDF, prop);
						axioms.add(ofp);
					}
					// get super properties
					for (Iterator iter = prop.getSuperProperties(ontSet).iterator(); iter.hasNext();) {
						OWLProperty supProp = (OWLProperty) iter.next();
						OWLSubPropertyAxiomImpl ospa = new OWLSubPropertyAxiomImpl((OWLDataFactoryImpl) ontDF, prop, supProp);
						axioms.add(ospa);
					}
					// get data range
					if (prop instanceof OWLDataProperty) {
						OWLDataProperty dprop = (OWLDataProperty) prop;
						Set ran = dprop.getRanges(ont);
						for (Iterator iter = ran.iterator(); iter.hasNext();) {
							OWLDataRange dr = (OWLDataRange) iter.next();
							OWLDataPropertyRangeAxiomImpl opra = new OWLDataPropertyRangeAxiomImpl((OWLDataFactoryImpl) ontDF, dprop, dr);
							axioms.add(opra);
						}					
					}
					else {
						// get object prop range
						OWLObjectProperty oprop = (OWLObjectProperty) prop;
						Set ran = oprop.getRanges(ont);
						for (Iterator iter = ran.iterator(); iter.hasNext();) {
							OWLDescription desc = (OWLDescription) iter.next();
							OWLObjectPropertyRangeAxiomImpl opra = new OWLObjectPropertyRangeAxiomImpl((OWLDataFactoryImpl) ontDF, oprop, desc);
							axioms.add(opra);
						}
						// get inverse
						Set inv = oprop.getInverses(ont);
						for (Iterator iter = inv.iterator(); iter.hasNext();) {
							OWLObjectProperty op = (OWLObjectProperty) iter.next();
							OWLInversePropertyAxiomImpl opra = new OWLInversePropertyAxiomImpl((OWLDataFactoryImpl) ontDF, oprop, op);
							axioms.add(opra);
						}
						// get attributes
						if (oprop.isTransitive(ont)) {
							OWLTransitivePropertyAxiomImpl ofp = new OWLTransitivePropertyAxiomImpl((OWLDataFactoryImpl) ontDF, oprop);
							axioms.add(ofp);
						}
						if (oprop.isSymmetric(ont)) {
							OWLSymmetricPropertyAxiomImpl ofp = new OWLSymmetricPropertyAxiomImpl((OWLDataFactoryImpl) ontDF, oprop);
							axioms.add(ofp);
						}
						if (oprop.isInverseFunctional(ont)) {
							OWLInverseFunctionalPropertyAxiomImpl ofp = new OWLInverseFunctionalPropertyAxiomImpl((OWLDataFactoryImpl) ontDF, oprop);
							axioms.add(ofp);
						}
					}
				}
				else if (entity instanceof OWLIndividual) {
					OWLIndividual ind = (OWLIndividual) entity;
					// add types of individual
					for (Iterator iter2 = ind.getTypes(ontSet).iterator(); iter2.hasNext();) {
						OWLDescription type = (OWLDescription) iter2.next();
						OWLIndividualTypeAssertionImpl ota = new OWLIndividualTypeAssertionImpl((OWLDataFactoryImpl) ontDF, ind, type);
						axioms.add(ota);					
					}
					// add data prop assertions
					Map dTypeVals = ind.getDataPropertyValues(ontSet);
					for (Iterator iter2 = dTypeVals.keySet().iterator(); iter2.hasNext();) {
						OWLDataProperty prop = (OWLDataProperty) iter2.next();
						Set values = (HashSet) dTypeVals.get(prop);
						for (Iterator iter3=values.iterator(); iter3.hasNext();) {
							OWLDataValue value = (OWLDataValue) iter3.next();
							OWLDataPropertyInstanceImpl odp = new OWLDataPropertyInstanceImpl((OWLDataFactoryImpl) ontDF, ind, prop, value);
							axioms.add(odp);
						}
					}
					// add object prop assertions
					Map oTypeVals = ind.getObjectPropertyValues(ontSet);
					for (Iterator iter2 = oTypeVals.keySet().iterator(); iter2.hasNext();) {
						OWLObjectProperty prop = (OWLObjectProperty) iter2.next();
						Set values = (HashSet) oTypeVals.get(prop);
						for (Iterator iter3=values.iterator(); iter3.hasNext();) {
							OWLIndividual value = (OWLIndividual) iter3.next();
							OWLObjectPropertyInstanceImpl odp = new OWLObjectPropertyInstanceImpl((OWLDataFactoryImpl) ontDF, ind, prop, value);
							axioms.add(odp);
						}
					}
					// add same and different individual axioms
					for (Iterator iter2 = ont.getIndividualAxioms().iterator(); iter2.hasNext();) {
						OWLIndividualAxiom ax = (OWLIndividualAxiom) iter2.next();
						if (ax instanceof OWLSameIndividualsAxiom) {
							OWLSameIndividualsAxiom osi = (OWLSameIndividualsAxiom) ax;
							if (osi.getIndividuals().contains(ind)) axioms.add(ax);
						}
						else if (ax instanceof OWLDifferentIndividualsAxiom) {
							OWLDifferentIndividualsAxiom osi = (OWLDifferentIndividualsAxiom) ax;
							if (osi.getIndividuals().contains(ind)) axioms.add(ax);
						} 
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			return axioms;
		}
/*
		public boolean checkLocality(OWLPropertyAxiom ax, Set sig) throws OWLException{
			return true;
		}
*/
		
		public boolean checkLocality(OWLPropertyAxiom ax, Set sig) throws OWLException{
			if (ax instanceof OWLSubPropertyAxiom){
				if (sig.contains(((OWLSubPropertyAxiom)ax).getSubProperty())){
					return false;
				}
				else
					return true;
			}
			if (ax instanceof OWLEquivalentPropertiesAxiom){
				Set eqproperties = ((OWLEquivalentPropertiesAxiom)ax).getProperties();
				Iterator i = eqproperties.iterator();
				while(i.hasNext()){
					OWLProperty prop = (OWLProperty)i.next();
					if(sig.contains(prop)){
						return false;
					}
				}
				return true;
			}
			if (ax instanceof OWLFunctionalPropertyAxiom){
				if(sig.contains(((OWLFunctionalPropertyAxiom)ax).getProperty() ))
					return false;
				else
					return true;
			}
			if(ax instanceof OWLTransitivePropertyAxiom){
				if(sig.contains(((OWLTransitivePropertyAxiom)ax).getProperty() ))
					return false;
				else
					return true;
				
			}
			if(ax instanceof OWLSymmetricPropertyAxiom){
				if(sig.contains(((OWLSymmetricPropertyAxiom)ax).getProperty() ))
					return false;
				else
					return true;
			}
			if(ax instanceof OWLInversePropertyAxiom){
				if(sig.contains(((OWLInversePropertyAxiom)ax).getProperty() ))
					return false;
				if (sig.contains(((OWLInversePropertyAxiom)ax).getInverseProperty() ))
					return false;
				else
					return true;
		
			}
			if(ax instanceof OWLPropertyDomainAxiom){
				if(!sig.contains(((OWLPropertyDomainAxiom)ax).getProperty() ))
					return true;
				else{
					if(isNegativelyLocal(((OWLPropertyDomainAxiom)ax).getDomain(),sig))
						return true;
						else
							return false;
				}
			}
			
			if(ax instanceof OWLObjectPropertyRangeAxiom){
				if(!sig.contains(((OWLObjectPropertyRangeAxiom)ax).getProperty() ))
					return false;
				else{
					return true;
				}
			}
			
			return true;
		
		}
		
		/*
		public boolean checkLocality(OWLClassAxiom ax, Set sig) throws Exception{
			return true;
		}
		*/
		
		public void saveAxiom(OWLClassAxiom ax, String path) throws OWLException, FileNotFoundException, IOException{
			OWLOntBuilder ob = new OWLOntBuilder();
			ax.accept(ob);
			OWLOntology temp = ob.currentOnt;
			
			saveOntologyToDisk(temp, path);
		}
		
		public void saveSignatureToDisk(Set sig, String path) throws FileNotFoundException, IOException, OWLException{
			File wkspcFile = new File(path);
			
			//swoopModel.setWkspcFile(wkspcFile); 
			ObjectOutputStream outs = new ObjectOutputStream(
					new FileOutputStream(wkspcFile));
			
			Iterator iter = sig.iterator();
			while(iter.hasNext()){
				OWLEntity ent = (OWLEntity)iter.next();
				outs.writeObject(ent.getURI().toString());
			}
		}
		
		
		public boolean isPositivelyLocal(OWLDescription desc, Set sig) throws OWLException{
			
			if(desc.equals(this.nothing))
				return true;
			if(desc.equals(this.thing))
				return false;
			
			if (desc instanceof OWLClass){
				if (sig.contains(desc))
					return false;
				else
					return true;
			}
			if (desc instanceof OWLDataAllRestriction){
				return false;
			}
			if(desc instanceof OWLDataSomeRestriction){
				return true;
			}
						
			if(desc instanceof OWLObjectSomeRestriction){
					if(!sig.contains(((OWLObjectSomeRestriction)desc).getProperty())){
						return true;
					}
					else{
						if(isPositivelyLocal(((OWLObjectSomeRestriction)desc).getDescription(),sig))
							return true;		
						else
							return false;
					}
					
				}
				if(desc instanceof OWLEnumeration){
					Set indiv = new HashSet();
					indiv = ((OWLEnumeration)desc).getIndividuals();
		 			boolean b = true;
					Iterator it = indiv.iterator();
					while(it.hasNext()){
						OWLIndividual in = (OWLIndividual)it.next();
						if(sig.contains(in)){
							return false;
						}
					}
					return b;
				}
				
				if(desc instanceof OWLObjectValueRestriction){
					if(!sig.contains(((OWLObjectValueRestriction)desc).getProperty())){
						return true;
					}
					else{
						if(!sig.contains(((OWLObjectValueRestriction)desc).getIndividual()))
							return true;		
						else
							return false;
					}
					
				}
				
										
				if(desc instanceof OWLObjectAllRestriction){
					return false;
				}
				
				if (desc instanceof OWLCardinalityRestriction)
				{
					try {
						if(((OWLCardinalityRestriction)desc).isAtMost())
							return false;
						if(((OWLCardinalityRestriction)desc).isAtLeast() || ((OWLCardinalityRestriction)desc).isExactly())
							if(!sig.contains(((OWLCardinalityRestriction)desc).getProperty()))
								return true;
							else
								return false;
						else
							return false;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				if (desc instanceof OWLNot){
					try {
						if(this.isPositivelyLocal(((OWLNot)desc).getOperand(), sig))
								return false;
						if(this.isNegativelyLocal(((OWLNot)desc).getOperand(), sig))
								return true;
						return false;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//At least one conjunct must be local
				if (desc instanceof OWLAnd){
					Iterator iter;
					try {
						iter = ((OWLAnd)desc).getOperands().iterator();
						while(iter.hasNext()){
							OWLDescription conjunct = (OWLDescription)iter.next();
							if (this.isPositivelyLocal(conjunct, sig))
								return true;
								
						}
						return false;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
					}
					
					return false;
				}
				
				//All disjuncts must be local
				if (desc instanceof OWLOr){
					Iterator iter;
					try {
						iter = ((OWLOr)desc).getOperands().iterator();
						while(iter.hasNext()){
							OWLDescription disjunct = (OWLDescription)iter.next();
							if (!(this.isPositivelyLocal(disjunct, sig)))
								return false;
								
						}
						return true;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return true;
				}
					
			
			return true;	
		
		}
		
		
		public boolean isNegativelyLocal(OWLDescription desc, Set sig) throws OWLException {
			
			if(desc.equals(this.nothing))
				return false;
			
			if(desc.equals(this.thing))
				return true;
			
			
			if(desc instanceof OWLClass)
				return false;
			
			if(desc instanceof OWLDataSomeRestriction)
				return false;
			
			if(desc instanceof OWLDataAllRestriction)
				return true;
			
			if(desc instanceof OWLObjectSomeRestriction)
				return false;
			
				
			if(desc instanceof OWLObjectValueRestriction){
				return false;
			}
				
			
			
			if(desc instanceof OWLEnumeration){
				return false;
			}
				
			if(desc instanceof OWLObjectAllRestriction){
				if(!sig.contains(((OWLObjectAllRestriction)desc).getProperty())){
					return true;
				}
				else{
					if(isNegativelyLocal(((OWLObjectAllRestriction)desc).getDescription(),sig))
						return true;		
					else
						return false;
				}
			}
				
			if (desc instanceof OWLCardinalityRestriction)
			{
				try {
					if(((OWLCardinalityRestriction)desc).isAtLeast())
						return false;
					if(((OWLCardinalityRestriction)desc).isAtMost())
						if(!sig.contains(((OWLCardinalityRestriction)desc).getProperty()))
							return true;
						else
							return false;
									
				} catch (OWLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				}
				
				if (desc instanceof OWLNot){
					try {
						if(this.isNegativelyLocal(((OWLNot)desc).getOperand(), sig))
								return false;
						if(this.isPositivelyLocal(((OWLNot)desc).getOperand(), sig))
								return true;
						return false;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//All conjuncts must be non-local
				if (desc instanceof OWLAnd){
					Iterator iter;
					try {
						iter = ((OWLAnd)desc).getOperands().iterator();
						while(iter.hasNext()){
							OWLDescription conjunct = (OWLDescription)iter.next();
							if (!this.isNegativelyLocal(conjunct, sig))
								return false;
								
						}
						return true;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
					}
					
					return false;
				}
				
				//At least one of the disjuncts must be non-local
				if (desc instanceof OWLOr){
					Iterator iter;
					try {
						iter = ((OWLOr)desc).getOperands().iterator();
						while(iter.hasNext()){
							OWLDescription disjunct = (OWLDescription)iter.next();
							if (this.isNegativelyLocal(disjunct, sig))
								return true;
				
						}
						return false;
					} catch (OWLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return true;
				}
				
			return true;	
		}
		
		
//		Input: Set of all axioms in the ontology.
	    //       Map from axioms in the ontology to signature
		//       Set of all classes in the ontology
		//Output: Map from class names to the signature of their module.
		public Map computeSignatureDependenciesOptimized(Set allAxioms, Map sigToAxioms, Map axSignature, Set allClasses) throws Exception{
			Map result = new HashMap();
			Set alreadyProcessed = new HashSet();
			int countModules = 0;
			int largestModule = 0;
			int countLargeModules = 0;
			int countClasses = 0;
			int skippedClasses = 0;
			
			Iterator it = allClasses.iterator();
			while(it.hasNext()){
				Set sigModule = new HashSet();
				OWLClass cl = (OWLClass)it.next();
				if(!alreadyProcessed.contains(cl)){
					countClasses++;
					if(DEBUG)
						System.out.println("Class: " + cl.getURI().toString());
					sigModule.add(cl);
					//alreadyProcessed.add(cl);
					sigModule.addAll(expandSignature(sigModule,sigToAxioms, axSignature, result));
					if(sigModule.size()> largestModule){
						largestModule = sigModule.size();
					}
					result.put(cl, sigModule);
					countModules++;
					if(sigModule.size()> 500)
						countLargeModules++;
					alreadyProcessed.addAll(sigModule);
					if(DEBUG)
						System.out.println("Size: " + sigModule.size());
				//renderSignature(sigModule);
					if(DEBUG)
						System.out.println(" NUMBER OF MODULES: " + countModules);
				}
				else{
					skippedClasses++;
				}
				if(DEBUG){
					System.out.println("Classes Processed: " + alreadyProcessed.size());
					System.out.println("NUMBER OF LOCALITY CHECKS " + nlocalityChecks);
				}
			}
			
			
			
			if(DEBUG){
				System.out.println("Classes Processed: " + countClasses);
				System.out.println("Classes skipped: " + skippedClasses);
				System.out.println("LARGEST module so far " + largestModule);
				System.out.println("NUMBER OF LARGE modules " + countLargeModules);
				System.out.println("NUMBER OF LOCALITY CHECKS " + nlocalityChecks);
			}
			return result;
		}
		
		
		//Input: Set of all axioms in the ontology.
	    //       Map from axioms in the ontology to signature
		//       Set of all classes in the ontology
		//Output: Map from class names to the signature of their module.
		/*
		public Map computeSignatureDependencies(Set allAxioms, Map axSignature, Set allClasses) throws Exception{
			Map result = new HashMap();
			Iterator it = allClasses.iterator();
			while(it.hasNext()){
				Set sigModule = new HashSet();
				OWLClass cl = (OWLClass)it.next();
				System.out.println("Class: " + cl.getURI().toString());
				sigModule.add(cl);
				sigModule = getModuleSignature(allAxioms, sigModule, axSignature);
				result.put(cl, sigModule);
				System.out.println("Size: " + sigModule.size());
				//renderSignature(sigModule);
			}
			return result;
		}
		*/
		
		public Set getModuleSignature(Set allAxioms, Set sigModule, Map axSignature) throws Exception{
				Set sigAux = new HashSet();
				sigAux.addAll(sigModule);
				Iterator iter = allAxioms.iterator();
				boolean changed = false;
				while(iter.hasNext()){
					OWLObject axiom = (OWLObject)iter.next(); 
					Set sigAxiom = new HashSet();
					//We retrieve the signature of the axiom
					if(axSignature.containsKey(axiom))
						sigAxiom = (Set)axSignature.get(axiom);
					else
						System.out.println("Error in signature");
			
					if(axiom instanceof OWLClassAxiom){
		            		if(!checkLocalitySyntax((OWLClassAxiom)axiom, sigModule)){
		            			sigModule.addAll(sigAxiom);
		            			changed = true;
		            	}
		            
		            }
		            if(axiom instanceof OWLPropertyAxiom){
		                if(!checkLocality((OWLPropertyAxiom)axiom, sigModule)){
		                	sigModule.addAll(sigAxiom);
	            			changed = true;
		            	}
		            }
				}
				if (changed){
					if(sigAux.size() == sigModule.size()){
						return sigModule;
					}
					else{
						if(DEBUG)
							System.out.println("I am repeating the big loop!");
						sigModule.addAll(getModuleSignature(allAxioms,sigModule,axSignature));
						
					}
			}
			return sigModule;
		}
		
		public boolean getCachedLocality(OWLObject axiom, Set sig){
				Map auxMap = new HashMap();
				auxMap =(Map)testedLocality.get(axiom);
				Set key = auxMap.keySet();
				Iterator i = key.iterator();
				while(i.hasNext()){
				    Boolean b = (Boolean)i.next();
					Set auxSet = new HashSet();
					auxSet = (Set)auxMap.get(b);
					if(auxSet.contains(sig)){
						return b.booleanValue();
					}	
				}	
	      return true;
		}	
			
		
		
		public boolean localityAlreadyPerformed(OWLObject axiom, Set sig){
			if(testedLocality.containsKey(axiom)){
				Map auxMap = new HashMap();
				auxMap =(Map)testedLocality.get(axiom);
				Set key = auxMap.keySet();
				Iterator i = key.iterator();
				while(i.hasNext()){
				    Boolean b = (Boolean)i.next();
					Set auxSet = new HashSet();
					auxSet = (Set)auxMap.get(b);
					Iterator iter = auxSet.iterator();
					while(iter.hasNext()){
						Set explored = (Set)iter.next();
						if(explored.containsAll(sig)){
							return true;
						}
					}
				}	
			}
			return false;
		}
		
		/*
		public void updateLocalityTests(OWLObject axiom, Boolean b, Set sig){
			if(!testedLocality.containsKey(axiom)){
				Map aux = new HashMap();
				Set s = new HashSet();
				s.add(sig);
				aux.put(b,s);
		        testedLocality.put(axiom, aux); 
			}
			else{
				Map auxMap = new HashMap();
				auxMap =(Map)testedLocality.get(axiom);
				if(!auxMap.containsKey(b)){
					Set s2 = new HashSet();
					s2.add(sig);
					auxMap.put(b,s2);
					testedLocality.put(axiom,auxMap);
				}
				else{
					Set auxSet = new HashSet();
					auxSet = (Set)auxMap.get(b);
					Iterator iter = auxSet.iterator();
					boolean toAdd = true;
					while(iter.hasNext()){
						Set explored = (Set)iter.next();
						if(explored.containsAll(sig))
							toAdd = false;
					}
					if(toAdd){
						Set s = new HashSet();
						s.add(sig);
						auxSet.add(s);
						auxMap.put(b,auxSet);
						testedLocality.put(axiom,auxMap);
					}
				}
			}
		}
		*/
		
		public boolean checkLocalitySyntax(OWLClassAxiom axiom, Set sig) throws Exception{
			
			if (axiom instanceof OWLSubClassAxiom){
				OWLDescription sup = ((OWLSubClassAxiom)axiom).getSuperClass();
				OWLDescription sub = ((OWLSubClassAxiom)axiom).getSubClass();
				boolean b1 = isPositivelyLocal(sub, sig);
				boolean b2 = isNegativelyLocal(sup, sig);
				if(b1 || b2){
					//Boolean bo = new Boolean(true);
					//updateLocalityTests(axiom, bo, sig);
					return true;
				}
				else{
					//Boolean bo = new Boolean(false);
					//updateLocalityTests(axiom, bo, sig);
					return false;
				}
			}
			if (axiom instanceof OWLEquivalentClassesAxiom){
				Set eqclasses = ((OWLEquivalentClassesAxiom)axiom).getEquivalentClasses();
				Iterator iter = eqclasses.iterator();
				if(eqclasses.size() == 2){
					OWLDescription first = (OWLDescription)iter.next();
					OWLDescription second = (OWLDescription)iter.next();
					boolean b1 = isPositivelyLocal(first,sig);
					boolean b2 = isPositivelyLocal(second, sig);
					if((b1==true && b2 ==true) || (b1==false && b2 == false) ){
						//Boolean bo = new Boolean(true);
						//updateLocalityTests(axiom, bo, sig);
						return true;
					}
					else{
						//Boolean bo = new Boolean(false);
						//updateLocalityTests(axiom, bo, sig);
						return false;
					}		
					}
				}
			
		
			
			if(axiom instanceof OWLDisjointClassesAxiom){
				Set disjclasses = ((OWLDisjointClassesAxiom)axiom).getDisjointClasses();
				Iterator iter = disjclasses.iterator();
				while(iter.hasNext()){
					OWLDescription desc = (OWLDescription)iter.next();
					if(isPositivelyLocal(desc,sig)){
						//Boolean bo = new Boolean(true);
						//updateLocalityTests(axiom, bo, sig);
						return true;
					}
				}
				//Boolean bo = new Boolean(false);
				//updateLocalityTests(axiom, bo, sig);
				return false;
			}
				
			return true;
		}
		
		
		public boolean checkLocality(OWLClassAxiom ax, Set sig) throws Exception{
			//This is for debugging purposes
			String path = "C:/ontologies/problematicAxiom.owl";
			saveAxiom(ax, path);
			String pathSig = "C:/ontologies/signature.txt";
			saveSignatureToDisk(sig, pathSig);
			//
			PelletReasoner reasoner = new PelletReasoner();
			if (DEBUG)
				System.out.println("Replacing axiom by Bottom");
			OWLClassAxiom axiom= replaceBottom(ax, sig);
			//This is for Debugging Purposes
			path = "C:/ontologies/problematicBottom.owl";
			//saveAxiom(axiom, path);
			//
			if (DEBUG)
				System.out.println("DONE Replacing axiom by Bottom");
			
			if (ax instanceof OWLSubClassAxiom){
					OWLDescription sup = ((OWLSubClassAxiom)axiom).getSuperClass();
					OWLDescription sub = ((OWLSubClassAxiom)axiom).getSubClass();
					if (DEBUG)
						System.out.println("Calling the Reasoner");
					if(reasoner.isSubClassOf(sub,sup)){
						if (DEBUG)
							System.out.println("DONE Calling the Reasoner");
						if(DEBUG)
							System.out.println("The SUBCLASS axiom is local w.r.t. the external signature");
						return true;
					}
					else{
						if(DEBUG)
							System.out.println("The SUBCLASS axiom is NOT local w.r.t. the external signature");
						return false;
					
					}
			}
			if (ax instanceof OWLEquivalentClassesAxiom){
				Set eqclasses = ((OWLEquivalentClassesAxiom)axiom).getEquivalentClasses();
				Iterator iter = eqclasses.iterator();
				if(eqclasses.size() == 2){
					OWLDescription first = (OWLDescription)iter.next();
					OWLDescription second = (OWLDescription)iter.next();
					//if(isObviousEquivalence((OWLEquivalentClassesAxiom)axiom)){
						//return true;
					//}
					if (DEBUG)
						System.out.println("Calling the Reasoner");
					if(reasoner.isEquivalentClass(first, second)){
						if (DEBUG)
							System.out.println("DONE Calling the Reasoner");
						if(DEBUG)
							System.out.println("The EQUIVALENTCLASSES axiom is local w.r.t. the external signature");
						return true;
					}
					else{
						if(DEBUG)
							System.out.println("The EQUIVALENTCLASSES axiom is NOT local w.r.t. the external signature");
						return false;
					}
				}
				else
					return true;
			}
			if (ax instanceof OWLDisjointClassesAxiom){
				Set disjclasses = ((OWLDisjointClassesAxiom)axiom).getDisjointClasses();
				OWLDataFactory df = source.getOWLDataFactory();
				OWLDescription conjunction = df.getOWLAnd(disjclasses);
				if (DEBUG)
					System.out.println("Calling the Reasoner");
				if(reasoner.isEquivalentClass(conjunction,this.nothing)){
					if (DEBUG)
						System.out.println("DONE Calling the Reasoner");
					if(DEBUG)
						System.out.println("The DISJOINTCLASSES axiom is local w.r.t. the external signature");
					return true;
			    }
                else{
					if(DEBUG)
						System.out.println("The DISJOINTCLASSES axiom is NOT local w.r.t. the external signature");
					return false;
				
				}
			}
		
			 
			
			if(DEBUG)
				System.out.println("Something WRONG");
			return true;
		}
		
		public OWLOntology getOntologyFromAxioms(Set s, URI uri) throws URISyntaxException, OWLException{
			OWLOntology module;
			OWLDataFactory df = null;
			OWLBuilder builder = new OWLBuilder();
			builder.createOntology(uri, uri);
			module = builder.getOntology();
			df = module.getOWLDataFactory();
			//addAnnotations(df);
			OWLClass thing = df.getOWLThing();
			AddEntity ae = new AddEntity(module, thing, null);
			ae.accept((ChangeVisitor) module);
			Iterator iter = s.iterator();
			while(iter.hasNext()){
				OWLObject axiom = (OWLObject)iter.next();
				if(axiom instanceof OWLClassAxiom){
					AddClassAxiom aax = new AddClassAxiom(module,(OWLClassAxiom)axiom,null);
					aax.accept((ChangeVisitor) module);
				}
				if(axiom instanceof OWLPropertyAxiom){
					if(axiom instanceof OWLFunctionalPropertyAxiom){
						OWLProperty p = ((OWLFunctionalPropertyAxiom)axiom).getProperty();
						SetFunctional change = new SetFunctional(module,p,true,null);
						change.accept((ChangeVisitor) module);
					}
					else{
						if(axiom instanceof OWLTransitivePropertyAxiom){
							OWLObjectProperty p = (OWLObjectProperty) ((OWLTransitivePropertyAxiom)axiom).getProperty();
							SetTransitive change2 = new SetTransitive(module,p,true,null);
							change2.accept((ChangeVisitor) module);
						}
						else{
							if(axiom instanceof OWLSymmetricPropertyAxiom){
								OWLObjectProperty p = (OWLObjectProperty) ((OWLTransitivePropertyAxiom)axiom).getProperty();
								SetSymmetric change2 = new SetSymmetric(module,p,true,null);
								change2.accept((ChangeVisitor) module);
							}
							else{
								AddPropertyAxiom aax = new AddPropertyAxiom(module,(OWLPropertyAxiom)axiom,null);
								aax.accept((ChangeVisitor) module);
							
							}
						}
					}
					
				}
				
    		}
			return module;
		}
		
		//Returns the collection of Axioms in an ontology
		public Set getAxiomsInOntology(OWLOntology ont) throws OWLException{
			Set result = new HashSet();
			AxiomCollector coll = new AxiomCollector(ont);
			result = coll.axiomize(ont);
			return result;
		}
		
		public Set expandSignature(Set processed, Map sigToAxioms, Map axSignature, Map moduleMap) throws Exception{
			int niterations = 0;
			//Set processed = new HashSet();
			Set toDo = new HashSet();
			toDo.addAll(processed);
			//toDo.addAll(inputSig);
			Set newSig = new HashSet();
			//newSig.addAll(updateSignature(processed, toDo, sigToAxioms, axSignature, moduleMap));
			//if(newSig.isEmpty()){
				//return inputSig;
			//}
			//else{
				//HashSet newSigAux = new HashSet();
				//newSigAux.addAll(newSig);
			  newSig.addAll(processed);	
			  while(!newSig.isEmpty()){
					processed.addAll(toDo);
					toDo = new HashSet();
					toDo.addAll(newSig);
				    newSig = new HashSet();
					newSig.addAll(updateSignature(processed,toDo, sigToAxioms, axSignature, moduleMap));
					niterations++;
					processed.addAll(toDo);
					
		
				//}
				//processed.addAll(newSigAux);
				
			}
			  if(DEBUG)
				  System.out.println("Times going through all axioms" + niterations );
			return processed;
		}
		
		public Set updateSignature(Set processed, Set toDo, Map sigToAxioms, Map axSignature, Map moduleMap) throws Exception{
			int avoidedTests =  0;
			Set newSig = new HashSet();
			Set axioms = new HashSet();
			
		    boolean changed = false;
			//Expand toDo list
		    Set toDoAux = new HashSet();
		    toDoAux.addAll(toDo);
		    //This is just an optimization
		    
		    Iterator k = toDoAux.iterator();
		    while(k.hasNext()){
		    	OWLEntity ent2 = (OWLEntity)k.next();
		    	if(moduleMap.containsKey(ent2)){
		    		Set auxi = (Set)moduleMap.get(ent2);
		    		toDo.addAll(auxi);
		    	}
		    }
		   
		   //
		    Set allSig = new HashSet(); 
		    allSig.addAll(toDo);
		    allSig.addAll(processed);
//		  Iterate over toDO list
			
		    Iterator iter = toDo.iterator();
			while(iter.hasNext()){
				OWLEntity ent = (OWLEntity)iter.next();
				Set aux = new HashSet();
				aux.addAll((Set)sigToAxioms.get(ent));
				axioms.addAll(aux);
			}
			
		    if(DEBUG){
		    	System.out.println("Number of axioms we iterate over: " + ": " +axioms.size());
		    	System.out.println("Size of processed signature: " + processed.size());
		    }
			Iterator it = axioms.iterator();
			while(it.hasNext()){
				OWLObject ax = (OWLObject)it.next();
				Set sigAxiom = new HashSet();
				//We retrieve the signature of the axiom
				sigAxiom.addAll((Set)axSignature.get(ax));
				if(!allSig.containsAll(sigAxiom)){
					if(ax instanceof OWLClassAxiom){
						//if(!localityAlreadyPerformed(ax,allSig)){
							nlocalityChecks++;
							if(!checkLocalitySyntax((OWLClassAxiom)ax, allSig)){
								newSig.addAll(sigAxiom);
								changed = true;
							}
						//}
						//else{
							//if(!getCachedLocality(ax, allSig)){
								//newSig.addAll(sigAxiom);
								//changed = true;
								//System.out.println("I am SKIPPING a locality check");
							//}
						//}
            
					}
				
					if(ax instanceof OWLPropertyAxiom){
						nlocalityChecks++;
						if(!checkLocality((OWLPropertyAxiom)ax, allSig)){
							newSig.addAll(sigAxiom);
							changed = true;
						}
					}
				}
				else
					avoidedTests++;
			}
			newSig.removeAll(processed);
			newSig.removeAll(toDo);
			if(DEBUG)
				System.out.println("Avoided locality tests: " + avoidedTests);
			return newSig;
		}
		
		
		//Creates a Map:
		// Key: Concept names in the ontology
		// Value: Set of axioms that mention that concept
		public Map signatureToAxioms(Set allAxioms, Set allEntities){
			Map result = new HashMap();
			Set ax = new HashSet();
			Iterator it = allEntities.iterator();
			//create the KeySet
			while(it.hasNext()){
				OWLEntity ent = (OWLEntity)it.next();
				result.put(ent, ax);
			}
			Iterator iter = allAxioms.iterator();
			while(iter.hasNext()){
				OWLObject axiom = (OWLObject)iter.next();
				Set sig = new HashSet();
				sig.addAll(getAxiomSignature(axiom, source));
				Iterator j = sig.iterator();
				while(j.hasNext()){
					OWLEntity cl2 = (OWLEntity)j.next();
					Set aux = new HashSet();
					if(result.containsKey(cl2))
						aux.addAll((Set)result.get(cl2));
					if(!aux.contains(axiom)){
							aux.add(axiom);
							result.put(cl2,aux);
					}
					
				}
			}
			return result;
		}
		
		//Creates a map from axioms in the ontology to their signature
		public Map axiomsToSignature(Set allAxioms){
			Map map = new HashMap();
			Iterator iter = allAxioms.iterator();
			while(iter.hasNext()){
				OWLObject axiom = (OWLObject)iter.next();
				Set sig = getAxiomSignature(axiom, source);
				map.put(axiom, sig);
			}
			return map;
		}
		
		public Set getOntologySignature(Set allAxioms, OWLOntology ont){
			Set result = new HashSet();
			Iterator iter = allAxioms.iterator();
			while(iter.hasNext()){
				OWLObject axiom = (OWLObject)iter.next();
				Set sig = getAxiomSignature(axiom, ont);
				result.addAll(sig);
			}
			return result;
		}
		
		
		//Having the signature dependencies for an entity, returns its module as a collection
		//of axioms.
		public Set getModuleFromSignature(Set allAxioms, Set sig, Map axSignature){
			Set result = new HashSet();
			Iterator iter = allAxioms.iterator();
			while(iter.hasNext()){
	        	OWLObject axiom = (OWLObject)iter.next(); 
	        	Set sigAxiom = new HashSet();
				if(axSignature.containsKey(axiom))
					sigAxiom = (Set)axSignature.get(axiom);
				else
					System.out.println("Error in signature");
				if(sig.containsAll(sigAxiom)){
					
					result.add(axiom);
				}
			}
			return result;
			
		}
		
		//Returns the Set of Axioms in the Module
		public Set getModule(Set allAxioms, Set si, Map axSignature) throws Exception{
			//Create Ontology
			Set siAux = new HashSet();
			siAux.addAll(si);
			boolean changed = false;
			Set result = new HashSet();
			
			Iterator iter = allAxioms.iterator();
			if(DEBUG)
				System.out.println("Starting Loop on Axioms");
			while(iter.hasNext()){
	            
				OWLObject axiom = (OWLObject)iter.next(); 
				
				Set sig = new HashSet();
				if(axSignature.containsKey(axiom))
					sig = (Set)axSignature.get(axiom);
				else
					System.out.println("Error in signature");
				
								
			   	if(axiom instanceof OWLClassAxiom){
	            	if (DEBUG)
	            			System.out.println("Checking Locality of a Class Axiom");
	            		if(!checkLocalitySyntax((OWLClassAxiom)axiom, si)){
	            			result.add(axiom);
	            			si.addAll(sig);
	            			changed = true;
	            	}
	            
	            }
	            if(axiom instanceof OWLPropertyAxiom){
	            	if (DEBUG)
	            		System.out.println("Checking Locality of a Property Axiom");
	    	        if(!checkLocality((OWLPropertyAxiom)axiom, si)){
	    	        	//if (DEBUG)
	    	        		//System.out.println("DONE Checking Locality of a Property Axiom");
		    	       	result.add(axiom);
	            		si.addAll(sig);
	            		changed = true;
	            	}
	            }
	        }
			if(DEBUG)
				System.out.println("Finish Loop On Axioms");
			if (changed){
				if(siAux.size() == si.size()){
					return result;
				}
				else{
					if(DEBUG)
						System.out.println("I am repeating the big loop!");
					result.addAll(getModule(allAxioms,si,axSignature));
					
				}
		}
			
			
			return result;
		}
		
		//Returns the set of symbols used in a concept description
		/*
		public Set signatureOf(OWLDescription desc) throws OWLException{
			Set result = new HashSet();
			if (desc instanceof OWLClass){
				result.add(desc);		
				return result;
			}
			if (desc instanceof OWLNot){
				return(signatureOf(
						((OWLNot)desc).getOperand()));
			}
			if (desc instanceof OWLAnd){
				Iterator iter = ((OWLAnd)desc).getOperands().iterator();
				while(iter.hasNext()){
					OWLDescription d = (OWLDescription)iter.next();
					result.addAll(signatureOf(d));
				}
				return result;
			}
			
			if (desc instanceof OWLOr){
				Iterator iter = ((OWLOr)desc).getOperands().iterator();
				while(iter.hasNext()){
					OWLDescription d = (OWLDescription)iter.next();
					result.addAll(signatureOf(d));
				}
				return result;
			}
			
			if(desc instanceof OWLRestriction){
				result.add(((OWLRestriction)desc).getProperty());
				if(desc instanceof OWLObjectQuantifiedRestriction)
					result.addAll(signatureOf(((OWLObjectQuantifiedRestriction)desc).getDescription()));
				return result;
			}
			
				
			return result;
		}
		*/
	/*
		public Set signatureOf(Set s) throws OWLException{
			Set result = new HashSet();
			Iterator iter = s.iterator();
			while(iter.hasNext()){
				OWLDescription desc = (OWLDescription)iter.next();
				result.addAll(signatureOf(desc));
			}
			return result;
					
		}
	*/	
	/*	
		public Set getAxiomSignature2(OWLObject axiom, OWLOntology ont) {
		  Set result = new HashSet();
		  return result;
		}
		*/
		
//		 returns the entities in the signature of the axiom (Taken from SwoopModel)
		public Set getAxiomSignature(OWLObject axiom, OWLOntology ont) {
			
			Set entities = new HashSet();
			try {
				OWLOntBuilder ob = new OWLOntBuilder();
				axiom.accept(ob);
				OWLOntology temp = ob.currentOnt;
		
				for (Iterator iter2=temp.getClasses().iterator(); iter2.hasNext();) {
					OWLClass cla = (OWLClass) iter2.next();
					entities.add(ont.getClass(cla.getURI()));
				}
				for (Iterator iter2=temp.getDataProperties().iterator(); iter2.hasNext();) {
					OWLDataProperty prop = (OWLDataProperty) iter2.next();
					entities.add(ont.getDataProperty(prop.getURI()));
				}
				for (Iterator iter2=temp.getObjectProperties().iterator(); iter2.hasNext();) {
					OWLObjectProperty prop = (OWLObjectProperty) iter2.next();
					entities.add(ont.getObjectProperty(prop.getURI()));
				}
				for (Iterator iter2=temp.getIndividuals().iterator(); iter2.hasNext();) {
					OWLIndividual ind = (OWLIndividual) iter2.next();
					entities.add(ont.getIndividual(ind.getURI()));
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			return entities;
		}
		
		public boolean saveOntologyToDisk (OWLOntology ont, String path) throws FileNotFoundException, IOException, OWLException{
	        File wkspcFile = new File(path);
			
			//swoopModel.setWkspcFile(wkspcFile); 
			ObjectOutputStream outs = new ObjectOutputStream(
					new FileOutputStream(wkspcFile));
			
			CorrectedRDFRenderer rend = new CorrectedRDFRenderer();
			StringWriter st = new StringWriter();
			rend.renderOntology(ont, st);
			outs.writeObject(st.getBuffer().toString());
		
				// also add imports information to importChanges
				//for (Iterator impOntIter = ont.getIncludedOntologies().iterator(); impOntIter.hasNext();) {
					//OWLOntology impOnt = (OWLOntology) impOntIter.next();
					//ImportChange change = new ImportChange(ont.getURI(),impOnt.getURI());
					//importChanges.add(change);
				//}
			
			return true;
		}

		/*
		public Set signatureOf(OWLClassAxiom ax) throws OWLException{
			Set result = new HashSet();
			if (ax instanceof OWLSubClassAxiom){
				OWLDescription sup = ((OWLSubClassAxiom)ax).getSuperClass();
				OWLDescription sub = ((OWLSubClassAxiom)ax).getSubClass();
				result.addAll(signatureOf(sup));
				result.addAll(signatureOf(sub));
				return result;
			}
			if (ax instanceof OWLEquivalentClassesAxiom){
				Set equiv = ((OWLEquivalentClassesAxiom)ax).getEquivalentClasses();
				result.addAll(signatureOf(equiv));
			}
			if (ax instanceof OWLDisjointClassesAxiom){
				Set disjoint = ((OWLDisjointClassesAxiom)ax).getDisjointClasses();
				result.addAll(signatureOf(disjoint));
			}
			
			return result;
		}
*/
		
		public String renderModule(OWLOntology module) throws OWLException{
			String output;
			StringWriter rdfBuffer = new StringWriter();
			CorrectedRDFRenderer rdfRenderer = new CorrectedRDFRenderer();
			rdfRenderer.renderOntology(module, rdfBuffer);
			output = rdfBuffer.toString();
			return output;
		}
		
		public boolean isObviousBottom(OWLDescription desc) throws OWLException{
			if(desc.equals(this.nothing)){
				return true;
			}
			if(desc instanceof OWLAnd){
				Set conjuncts = ((OWLAnd)desc).getOperands();
				if(conjuncts.contains(this.nothing)){
					return true;
				}
			}
			
			return false;
		}
		
		public boolean isObviousEquivalence(OWLEquivalentClassesAxiom ax) throws OWLException{
			Set eqClasses = ax.getEquivalentClasses();
			Iterator iter = eqClasses.iterator();
			boolean allBottom = true;
			while(iter.hasNext()){
				OWLDescription desc = (OWLDescription)iter.next();
				if(!isObviousBottom(desc)){
					allBottom = false;
				}
			}
			if(allBottom == true)
				return true;
			else
				return false;
		}
		
		
		public void renderSignature(Set si) throws OWLException{
			Iterator iter = si.iterator();
			System.out.println("****Signature of the Axiom*******************");
			while(iter.hasNext()){
				OWLEntity ent = (OWLEntity)iter.next();
				System.out.println(ent.getURI().toString());
					
			}
			System.out.println("***End Signature of the Axiom********************");
			
		}


		public Map pruneModules(Map signatureTable) {
		    int largeModulesPruned = 0;
		    int smallModulesPruned = 0;
			Set toRemove = new HashSet();
		    Map auxMap = new HashMap();
		    //Generate a copy of the Map
		    auxMap.putAll(signatureTable);
		  //  Set usedSignature = new HashSet();
		    Iterator iter = signatureTable.keySet().iterator();
		    while(iter.hasNext()){
		    	OWLEntity ent = (OWLEntity)iter.next();
		    	Iterator it = auxMap.keySet().iterator();
		    	boolean b = false;
		    	while(it.hasNext()){
		    		OWLEntity entAux = (OWLEntity)it.next();
		    		if(b==true){
		    			Set sig = (Set)auxMap.get(entAux);
		    			if(sig.contains(ent)){
		    				toRemove.add(ent);
		    				if(sig.size()>200)
		    					largeModulesPruned++;
		    				else
		    					smallModulesPruned++;
		    			}
		    		}
		    		if(entAux.equals(ent)){
		    			b=true;
		    		}
		    	}
		    	    	
		    }
		    System.out.println("Modules to remove: " +toRemove.size());
		    System.out.println("Large modules pruned " + largeModulesPruned);
		    //Remove the entries from the Map
		    Iterator i = auxMap.keySet().iterator();
		    while(i.hasNext()){
		    	OWLEntity ent = (OWLEntity)i.next();
		    	if(toRemove.contains(ent)){
		    		signatureTable.remove(ent);
		    	}
		    }
		    
			return signatureTable;
		}

		/*
		public Map pruneModulesTest(Map signatureTable) {
		    int largeModulesPruned = 0;
		    int smallModulesPruned = 0;
			Set toRemove = new HashSet();
		    Map auxMap = new HashMap();
		    //Generate a copy of the Map
		    auxMap.putAll(signatureTable);
		  //  Set usedSignature = new HashSet();
		    Iterator iter = signatureTable.keySet().iterator();
		    while(iter.hasNext()){
		    	OWLEntity ent = (OWLEntity)iter.next();
		    	Iterator it = auxMap.keySet().iterator();
		    	while(it.hasNext()){
		    		OWLEntity entAux = (OWLEntity)it.next();
		    		if(!entAux.equals(ent)){
		    			Set sig = (Set)auxMap.get(entAux);
		    			if(sig.contains(ent)){
		    				toRemove.add(ent);
		    				if(sig.size()>200)
		    					largeModulesPruned++;
		    				else
		    					smallModulesPruned++;
		    			}
		    		}
		    		
		    	}
		    	    	
		    }
		    System.out.println("Modules to remove: " +toRemove.size());
		    System.out.println("Large modules pruned " + largeModulesPruned);
			return auxMap;
		}
*/
}