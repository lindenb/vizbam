package com.github.lindenb.vizbam;
import java.io.File;
import java.io.PrintWriter;

import com.github.lindenb.vizbam.locparser.LocParser;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.StandardOptionDefinitions;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Log;
import net.sf.samtools.BAMIndex;
import net.sf.samtools.SAMFileReader;

public class VizBamCmdLine extends CommandLineProgram
	{
	private static final Log log = Log.getInstance(VizBamCmdLine.class);
    @Usage
    public String USAGE = getStandardUsagePreamble() + "Print a SAM alignment like samtools tview";
    @Option(shortName="F", doc="output format",optional=true) 
    public String format="text";
    @Option(doc = "Input indexed reference", shortName = StandardOptionDefinitions.REFERENCE_SHORT_NAME)
    public File REFERENCE=null;
    @Option(doc = "out file", shortName = StandardOptionDefinitions.OUTPUT_SHORT_NAME,optional=true)
    public File OUT=null;
    
    
    @Option(shortName= StandardOptionDefinitions.INPUT_SHORT_NAME, doc="A BAM file to process.")
    public File INPUT=null;
    @Option(shortName= "P", doc="The position to process. Syntax is \"chrom:position\" the chromosome must be present in the reference .")
    public String position=null;
    @Option(shortName= "W", doc="The screen width.",optional=true)
    public int width=80;
    @Option(shortName= "U", doc="Use Clipped Ends.",optional=true)
    public boolean useclipped=false;
    @Option(shortName= "B", doc="Handle Base quality",optional=true)
    public boolean base_quality=false;
    
    public static void main(final String[] argv)
    	{
        new VizBamCmdLine().instanceMainWithExit(argv);
    	}	

    private void setuptVizBam(VizBam viz)
    	{
    	viz.setWidth(this.width);
    	viz.setUseClipped(this.useclipped);
    	viz.setHandleBaseQuality(base_quality);
    	}
    
    /**
     * Do the work after command line has been parsed.
     * RuntimeException may be thrown by this method, and are reported appropriately.
     *
     * @return program exit status.
     */
    @Override
    protected int doWork() {
    	if (INPUT.getName().endsWith(BAMIndex.BAMIndexSuffix))
    	log.warn("INPUT should be BAM file not index file");
    	IoUtil.assertFileIsReadable(INPUT);
    	IoUtil.assertFileIsReadable(REFERENCE);
    	if(position==null)
    		{
    		log.error("position was not specified");
    		return -1;
    		}
    	
    	
        ReferenceSequenceFile reference = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE);
    	SAMSequencePosition loc=LocParser.parseOne(reference.getSequenceDictionary(), position, false);
    	if(loc==null)
    		{
    		log.error("Cannot parse "+position);
    		return -1;
    		}
        
        
        SAMFileReader samReader = null;
        
        
        try
	        {
	        samReader=new SAMFileReader(INPUT);
	        
	        PrintWriter pw=(OUT!=null?new PrintWriter(OUT):new PrintWriter(System.out));
	        
	        if(format.equals("html") || format.equals("xml"))
	        	{
	    		AbstractXMLVizBam viz=null;
	    		if(format.equals("html"))
	    			{
	    			viz=new HTMLVizBam(samReader,reference);
	    			}
	    		else
	    			{
	    			viz=new XMLVizBam(samReader,reference);
	    			}
	        	
	    		setuptVizBam(viz);

	    		viz.align(loc.getName(), loc.getPosition());
	    		viz.print(pw);
	    		viz.close();
	        	}
	        else
	        	{
	        	TTYVizBam viz=new TTYVizBam(samReader,reference);
	        	setuptVizBam(viz);
	        	
	        	viz.setWriter(pw);
	        	viz.align(loc.getName(), loc.getPosition());
	        	viz.close();
	        	}
	        pw.flush();
			pw.close();
	        }
        catch(Exception err)
        	{
        	log.error(err);
        	return -1;
        	}
        finally
        	{
        	if(samReader!=null) samReader.close();
        	}
    	return 0;
    }

}
