/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.coeus.s2sgen.impl.generate.support;

import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument;
import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument.PHS398CoverPageSupplement;
import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument.PHS398CoverPageSupplement.ClinicalTrial;
import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument.PHS398CoverPageSupplement.ContactPersonInfo;
import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument.PHS398CoverPageSupplement.PDPI;
import gov.grants.apply.forms.phs398CoverPageSupplementV11.PHS398CoverPageSupplementDocument.PHS398CoverPageSupplement.StemCells;
import gov.grants.apply.system.globalLibraryV20.YesNoDataType;
import gov.grants.apply.system.globalLibraryV20.YesNoDataType.Enum;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.api.ynq.YnqConstant;
import org.kuali.coeus.common.questionnaire.api.answer.AnswerHeaderContract;
import org.kuali.coeus.propdev.api.person.ProposalPersonContract;
import org.kuali.coeus.propdev.api.person.ProposalPersonDegreeContract;
import org.kuali.coeus.propdev.api.ynq.ProposalYnqContract;
import org.kuali.coeus.s2sgen.impl.generate.FormGenerator;
import org.kuali.coeus.s2sgen.impl.generate.FormVersion;
import org.kuali.coeus.s2sgen.impl.person.DepartmentalPersonDto;
import org.kuali.coeus.propdev.api.core.ProposalDevelopmentDocumentContract;

import org.kuali.coeus.s2sgen.impl.util.FieldValueConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


import java.util.List;

/**
 * Class for generating the XML object for grants.gov
 * PHS398CoverPageSupplementV1_1. Form is generated using XMLBean classes and is
 * based on PHS398CoverPageSupplement schema.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
@FormGenerator("PHS398CoverPageSupplementV1_1Generator")
public class PHS398CoverPageSupplementV1_1Generator extends
		PHS398CoverPageSupplementBaseGenerator {

    @Value("http://apply.grants.gov/forms/PHS398_CoverPageSupplement-V1.1")
    private String namespace;

    @Value("PHS398_CoverPageSupplement-V1.1")
    private String formName;

    @Value("classpath:org/kuali/coeus/s2sgen/impl/generate/support/stylesheet/PHS398_CoverPageSupplement-V1.1.fo.xsl")
    private Resource stylesheet;

    @Value("gov.grants.apply.forms.phs398CoverPageSupplementV11")
    private String packageName;

    @Value("185")
    private int sortIndex;

    List<? extends AnswerHeaderContract> answerHeaders;

	/**
	 * 
	 * This method gives information of Cover Page Supplement such as PDPI
	 * details,Clinical Trail information,Contact person information.
	 * 
	 * @return coverPageSupplementDocument {@link XmlObject} of type
	 *         PHS398CoverPageSupplementDocument.
	 */
	private PHS398CoverPageSupplementDocument getCoverPageSupplement() {

		PHS398CoverPageSupplementDocument coverPageSupplementDocument = PHS398CoverPageSupplementDocument.Factory
				.newInstance();
		PHS398CoverPageSupplement coverPageSupplement = PHS398CoverPageSupplement.Factory
				.newInstance();
		answerHeaders = getPropDevQuestionAnswerService().getQuestionnaireAnswerHeaders(pdDoc.getDevelopmentProposal().getProposalNumber());
		coverPageSupplement.setFormVersion(FormVersion.v1_1.getVersion());
		coverPageSupplement.setPDPI(getPDPI());
		coverPageSupplement.setClinicalTrial(getClinicalTrial());
		coverPageSupplement.setContactPersonInfo(getContactPersonInfo());
		StemCells stemCells = getStemCells();
		coverPageSupplement.setStemCells(stemCells);
		coverPageSupplementDocument
				.setPHS398CoverPageSupplement(coverPageSupplement);
		return coverPageSupplementDocument;
	}

	/**
	 * 
	 * This method gives the personal details such as Name, New Investigator,
	 * Degrees of Principal Investigator
	 * 
	 * @return PDPI object containing Principal Investigator details.
	 */
	private PDPI getPDPI() {

		PDPI pdpi = PDPI.Factory.newInstance();
		ProposalPersonContract PI = s2SProposalPersonService.getPrincipalInvestigator(pdDoc);
		pdpi.setPDPIName(globLibV20Generator.getHumanNameDataType(PI));
		// Set default values for mandatory fields
		pdpi.setIsNewInvestigator(YesNoDataType.N_NO);

		ProposalYnqContract proposalYnq = getProposalYnQ(IS_NEW_INVESTIGATOR);
		if (PI != null) {

			if (proposalYnq != null) {
				YesNoDataType.Enum answer = null;
				if (proposalYnq.getAnswer() != null) {
					answer = (proposalYnq.getAnswer().equals(
							YnqConstant.YES.code()) ? YesNoDataType.Y_YES
							: YesNoDataType.N_NO);
					pdpi.setIsNewInvestigator(answer);
				}

			}
			String[] degreeArr = null;
			if (PI.getProposalPersonDegrees() != null) {
				degreeArr = new String[PI.getProposalPersonDegrees().size()];
			}
			int size = 0;
			for (ProposalPersonDegreeContract personDegree : PI
					.getProposalPersonDegrees()) {
				// Degrees: 0...3
				if (size > MAX_NUMBER_OF_DEGREES) {
					break;
				}
				if (personDegree.getDegree() != null) {
					if (personDegree.getDegree().length() > PERSON_DEGREE_MAX_LENGTH) {
						degreeArr[size] = personDegree.getDegree().substring(0,
								PERSON_DEGREE_MAX_LENGTH);
					} else {
						degreeArr[size] = personDegree.getDegree();
					}
					size++;
				}
			}
			pdpi.setDegreesArray(degreeArr);
		}
		return pdpi;
	}

	/**
	 * 
	 * This method is used to get Clinical Trial information
	 * 
	 * @return ClinicalTrial object containing Clinical Trail Details.
	 */
	private ClinicalTrial getClinicalTrial() {

	    ClinicalTrial clinicalTrial = ClinicalTrial.Factory.newInstance();
	    String answer = null;
	    String subAnswer = null;
	    answer = getAnswer(IS_CLINICAL_TRIAL,answerHeaders);
	    if (answer != null) {
	        if (!answer.equals(NOT_ANSWERED)) {
	            if (YnqConstant.YES.code().equals(answer)) {
	                clinicalTrial.setIsClinicalTrial(YesNoDataType.Y_YES);
	                subAnswer = getAnswer(PHASE_III_CLINICAL_TRIAL,answerHeaders);
	                if (subAnswer != null) {
	                    if (!subAnswer.equals(NOT_ANSWERED)) {
	                        if (YnqConstant.YES.code().equals(subAnswer)) {
	                            clinicalTrial.setIsPhaseIIIClinicalTrial(YesNoDataType.Y_YES);   
	                        } else {
	                            clinicalTrial.setIsPhaseIIIClinicalTrial(YesNoDataType.N_NO);   
	                        }

	                    }
	                }
	            } else {
	                clinicalTrial.setIsClinicalTrial(YesNoDataType.N_NO);
	            }
	        }
	    }
	    return clinicalTrial;
	}

	/**
	 * 
	 * This method gives the Contact person information such as contact
	 * Name,Phone,Fax,EmailAddress,Title,Address.
	 * 
	 * @return ContactPersonInfo object containing contact person details.
	 */
	private ContactPersonInfo getContactPersonInfo() {
		ContactPersonInfo contactPersonInfo = ContactPersonInfo.Factory
				.newInstance();
		DepartmentalPersonDto person = departmentalPersonService.getContactPerson(pdDoc);
		contactPersonInfo.setContactName(globLibV20Generator
				.getHumanNameDataType(person));
		contactPersonInfo.setContactPhone(person.getOfficePhone());
		if (StringUtils.isNotEmpty(person.getFaxNumber())) {
			contactPersonInfo.setContactFax(person.getFaxNumber());
		}
		if (person.getEmailAddress() != null
				&& !person.getEmailAddress().equals("")) {
			contactPersonInfo.setContactEmail(person
					.getEmailAddress());
		}
		contactPersonInfo.setContactTitle(person.getPrimaryTitle());
		contactPersonInfo.setContactAddress(globLibV20Generator
				.getAddressDataType(person));
		return contactPersonInfo;
	}

	/**
	 * 
	 * This method is used to get information of Stem Cells for the form
	 * PHS398CoverPage
	 * 
	 * @return StemCells object containing information about Human stem Cells
	 *         involvement.
	 */
	private StemCells getStemCells() {

	    StemCells stemCells = StemCells.Factory.newInstance();  
	    Enum answers = YesNoDataType.N_NO;
	    String childAnswer = null;  
	    String answer = getAnswer(IS_HUMAN_STEM_CELLS_INVOLVED,answerHeaders);
	    if (answer != null) {
	        if (!answer.equals(NOT_ANSWERED)) {
	            answers = YnqConstant.YES.code().equals(getAnswer(IS_HUMAN_STEM_CELLS_INVOLVED, answerHeaders)) ? YesNoDataType.Y_YES : YesNoDataType.N_NO;
	            if (YnqConstant.YES.code().equals(answer)) {
	                stemCells.setIsHumanStemCellsInvolved(YesNoDataType.Y_YES);
	                String subAnswer = getAnswer(SPECIFIC_STEM_CELL_LINE,answerHeaders);
	                if (subAnswer != null) {
	                    if(!subAnswer.equals(NOT_ANSWERED)) {
	                        if (YnqConstant.YES.code().equals(subAnswer)) {
	                            stemCells.setStemCellsIndicator(YesNoDataType.N_NO);
	                            childAnswer = getAnswers(REGISTRATION_NUMBER,answerHeaders);
	                        }
	                        else {
	                            stemCells.setStemCellsIndicator(YesNoDataType.Y_YES);
	                        }
	                    }
	                }
	                if (childAnswer != null) {
	                    if (FieldValueConstants.VALUE_UNKNOWN.equalsIgnoreCase(childAnswer)) {
	                        stemCells.setStemCellsIndicator(answers);
	                    } else {
	                        List<String> cellLines = getCellLines(childAnswer);
	                        if (cellLines.size() > 0) {
	                            stemCells.setCellLinesArray(cellLines.toArray(new String[0]));
	                        }
	                    }
	                }
	            } else {
	                stemCells.setIsHumanStemCellsInvolved(YesNoDataType.N_NO); 
	            }
	        }
	    }
	    return stemCells;
	}
	  
	/**
	 * This method creates {@link XmlObject} of type
	 * {@link PHS398CoverPageSupplementDocument} by populating data from the
	 * given {@link ProposalDevelopmentDocumentContract}
	 * 
	 * @param proposalDevelopmentDocument
	 *            for which the {@link XmlObject} needs to be created
	 * @return {@link XmlObject} which is generated using the given
	 *         {@link ProposalDevelopmentDocumentContract}
	 */
	public XmlObject getFormObject(
			ProposalDevelopmentDocumentContract proposalDevelopmentDocument) {
		this.pdDoc = proposalDevelopmentDocument;
		return getCoverPageSupplement();
	}

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public Resource getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(Resource stylesheet) {
        this.stylesheet = stylesheet;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }
}
