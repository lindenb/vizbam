package com.github.lindenb.vizbam;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.github.lindenb.vizbam.locparser.LocParser;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class TTYVizBam extends VizBam
	{
	private PrintWriter out=new PrintWriter(System.out);
	public TTYVizBam(File bamFile,
			IndexedFastaSequenceFile indexedFastaSequenceFile)
		{
		super(bamFile, indexedFastaSequenceFile);
		}

	public TTYVizBam(SAMFileReader samFileReader,
			IndexedFastaSequenceFile indexedFastaSequenceFile)
		{
		super(samFileReader, indexedFastaSequenceFile);
		}
	
	
	public PrintWriter getWriter()
		{
		return out;
		}
	

	@Override
	protected void startAlign(String chromName, int position)
		{
		
		}

	@Override
	protected void endAlign(String chromName, int position)
		{
		
		}
	
	@Override
	protected void startBase(char base, int qual) {
		
		}

	@Override
	protected void endBase() {
		
		}
	
	@Override
	protected void printReference(CharSequence reference)
		{
		getWriter().println(reference);
		
		}
	@Override
	protected void printRuler(CharSequence ruler)
		{
		getWriter().println(ruler);
		}


	@Override
	protected void startRow()
		{
		}

	@Override
	protected void endRow()
		{
		getWriter().println();
		}

	@Override
	protected void startSamRecord(SAMRecord record)
		{
		}

	@Override
	protected void endSamRecord(SAMRecord record)
		{
		}

	@Override
	protected void startCigar(SAMRecord record, CigarElement ce)
		{		
		}

	@Override
	protected void endCigar(SAMRecord record, CigarElement ce)
		{		
		}

	@Override
	protected void write(char c)
		{
		getWriter().print(c);
		}
	@Override
	public void close() {
		getWriter().flush();
		super.close();
		}
	
	
    public static void main(String[] args) throws Exception
	    {
	    LOG.setLevel(Level.OFF);
	    List<String> regions=new ArrayList<String>();
	    
	    args=new String[]{
	        "-L","ALL",   
	        "-r","1:897325",
			"-R","/commun/data/pubdb/broadinstitute.org/bundle/1.5/b37/human_g1k_v37.fasta",
			"/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD00172/CD00172_recal.bam"
	        };
	    File referenceFile=null;
	   int optind=0;
	   while(optind< args.length)
	       {
	       if(args[optind].equals("-h") ||
	          args[optind].equals("-help") ||
	          args[optind].equals("--help"))
	           {
	           System.err.println("Pierre Lindenbaum PhD. 2013");
	           System.err.println("Options:");
	           System.err.println(" -h help; This screen.");
	           System.err.println(" -R <fasta> Reference File.");
	           return;
	           }
	       else if(args[optind].equals("-r") && optind+1 < args.length)
	           {
	       		regions.add(args[++optind]);
	           }
	       else if(args[optind].equals("-R") && optind+1 < args.length)
	           {
	           referenceFile=new File(args[++optind]);
	           }
	       else if(args[optind].equals("-L") && optind+1 < args.length)
	           {
	           LOG.setLevel(Level.parse(args[++optind]));
	           }
	       else if(args[optind].equals("--"))
	           {
	           optind++;
	           break;
	           }
	       else if(args[optind].startsWith("-"))
	           {
	           System.err.println("Unknown option "+args[optind]);
	           return;
	           }
	       else
	           {
	           break;
	           }
	       ++optind;
	       }
	   
	   if(optind==args.length)
	   	{
	   	System.err.println("Illegal number of arguments");
	   	System.exit(-1);
	   	}
	   
	   if(referenceFile==null)
	   	{
	   	System.err.println("Reference file missing");
	   	System.exit(-1);
	   	}
	   IndexedFastaSequenceFile fsf=new IndexedFastaSequenceFile(referenceFile);
	   if(!fsf.isIndexed())
	   	{
	   	System.err.println("Reference file is not indexed.");
	   	System.exit(-1);
	   	}
	  
	   File f=new File(args[optind]);
	    
	   TTYVizBam app=new TTYVizBam(f,fsf);
	   
	   if(regions.isEmpty())
	   	{
	   	app.align(
	   			 fsf.getSequenceDictionary().getSequence(0).getSequenceName(),
	   			 1);
	   	}
	   for(String r: regions)
	   	{
	   	SAMSequencePosition pos=LocParser.parseOne(fsf.getSequenceDictionary(), r, true);
	   	if(pos==null)
	   		{
	   		System.err.println("Cannot parse region:"+r);
	   		System.exit(-1);
	   		}
	   	app.align(pos.getName(),pos.getPosition());
	   	}
	   
	   app.close();
	   }

	}
