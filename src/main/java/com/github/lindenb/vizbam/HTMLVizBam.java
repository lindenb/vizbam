package com.github.lindenb.vizbam;

import java.io.File;
import java.io.PrintWriter;

import org.w3c.dom.Element;

import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class HTMLVizBam extends AbstractXMLVizBam
	{
	public HTMLVizBam(File bamFile, ReferenceSequenceFile ref)
		{
		super(bamFile, ref);
		}

	public HTMLVizBam(SAMFileReader samFileReader,
			ReferenceSequenceFile indexedFastaSequenceFile)
		{
		super(samFileReader, indexedFastaSequenceFile);
		}
	
	protected Element createElement(String elementName,String clazz)
		{
		Element E=getDocument().createElement(elementName);
		if(clazz!=null) E.setAttribute("class", clazz);
		return E;
		}
	
	protected Element createDiv(String clazz)
		{
		return createElement("div",clazz);
		}
	
	@Override
	protected Element createReferenceElement() {
		return createDiv("samref");
		}

	@Override
	protected Element createRulerElement() {
		return createDiv("samruler");
	}

	@Override
	protected Element createRowElement(int rowIndex) {
		return createDiv("samrow"+(rowIndex%2));
	}

	@Override
	protected Element createRecordElement(SAMRecord record)
		{
		StringBuilder clazz=new StringBuilder();
		
		
		if(record.getReadPairedFlag())
			{
			clazz.append("samP ");
			if(record.getProperPairFlag()) clazz.append("samp ");
			if(record.getDuplicateReadFlag()) clazz.append("samD ");
			if(record.getFirstOfPairFlag()) clazz.append("sam1 ");
			if(record.getSecondOfPairFlag()) clazz.append("sam2 ");
			if(record.getMateNegativeStrandFlag())
				{
				clazz.append("samMr ");
				}
			else
				{
				clazz.append("samMf ");
				}
			if(record.getMateUnmappedFlag())
				{
				clazz.append("samMu ");
				}
			}
		if(record.getNotPrimaryAlignmentFlag()) clazz.append("samNPA ");
		if(record.getReadUnmappedFlag()) clazz.append("samRu ");
		if(record.getReadNegativeStrandFlag())
			{
			clazz.append("samRr ");
			}
		else
			{
			clazz.append("samRf ");
			}
		

		clazz.append("qual"+((int)Math.ceil(record.getMappingQuality()/10.0))*10);
		
		Element E= createElement("span",clazz.toString().trim());
		E.setAttribute("title",String.valueOf(record.getReadName()));
		return E;
		}
	
	
	@Override
	protected Element createBaseElement(char c, int qual) {
		return createElement("span","qual"+((int)Math.ceil(qual/10.0))*10);
		}
	
	@Override
	protected Element createCigarOperatorElement(SAMRecord record,
			CigarElement ce) {
		return createElement("span","cigar"+ce.getOperator().name());
		}

	
	
	}
