package com.github.lindenb.vizbam;

import java.io.File;
import java.io.PrintWriter;

import org.w3c.dom.Element;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class HTMLVizBam extends AbstractXMLVizBam
	{
	public HTMLVizBam(File bamFile, IndexedFastaSequenceFile ref)
		{
		super(bamFile, ref);
		}

	public HTMLVizBam(SAMFileReader samFileReader,
			IndexedFastaSequenceFile indexedFastaSequenceFile)
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

	
	public static void main(String[] args) throws Exception
		{
		args=new String[]{
				"-R","/commun/data/pubdb/broadinstitute.org/bundle/1.5/b37/human_g1k_v37.fasta",
				"-o","/home/lindenb/jeter.html",
				"/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD00172/CD00172_recal.bam"
			};
		File fileOut=null;
		File refFile=null;
		String loc=null;
		int optind=0;
		while(optind< args.length)
			{
			if(args[optind].equals("-h"))
				{
				System.err.println("Pierre Lindenbaum PhD. 2013.");
				System.err.println("Usage:");
				System.err.println("\tjava -jar blabla.jar -R");
				System.err.println("Options:");
				return;
				}
			else if(args[optind].equals("-R") && optind+1 < args.length)
				{
				refFile=new File(args[++optind]);
				}
			else if(args[optind].equals("-o") && optind+1 < args.length)
				{
				fileOut=new File(args[++optind]);
				}
			else if(args[optind].equals("-p") && optind+1 < args.length)
				{
				loc=args[++optind];
				}
			else if(args[optind].equals("--"))
				{
				optind++;
				break;
				}
			else if(args[optind].startsWith("-"))
				{
				loc=args[++optind];
				}
			else
				{
				break;
				}
			optind++;
			}
		if(optind==args.length)
			{
			System.err.println("BAM file missing");
			return;
			}
		if(refFile==null)
			{
			System.err.println("Ref file missing");
			return;
			}
		
		IndexedFastaSequenceFile reference=new IndexedFastaSequenceFile(refFile);
		File bamFile=new File(args[optind]);
		HTMLVizBam viz=new HTMLVizBam(bamFile,reference);
		viz.align("1", 897325);
		PrintWriter pw=(fileOut!=null?new PrintWriter(fileOut):new PrintWriter(System.out));
		pw.println("<html><body><pre>");
		viz.print(pw);
		viz.close();
		pw.println("</pre></body></html>");
		pw.flush();
		pw.close();
		}
	
	
	}
