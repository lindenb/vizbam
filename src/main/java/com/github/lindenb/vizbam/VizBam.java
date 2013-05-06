
package com.github.lindenb.vizbam;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



import net.sf.picard.filter.AggregateFilter;
import net.sf.picard.filter.DuplicateReadFilter;
import net.sf.picard.filter.FailsVendorReadQualityFilter;
import net.sf.picard.filter.NotPrimaryAlignmentFilter;
import net.sf.picard.filter.SamRecordFilter;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;

import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;

public abstract class VizBam implements Closeable
    {
    private static final char SPACE1=' ';  
    private static final char SPACE2=' ';  
    private static final char SPACE3=' ';  

    protected static final Logger LOG=Logger.getLogger("com.github.lindenb.vizbam");
    private SAMFileReader samFileReader;
    /** this class opened the samFileReader */
    private boolean samFileReaderOwner=false;
    private IndexedFastaSequenceFile indexedFastaSequenceFile;
    private SAMSequenceDictionary samSequenceDictionary;
    /** number of columns */
    private int nCols=80;
    /** user filter for SamRecord */
    private SamRecordFilter samRecordFilter=null;
    /** private should we handle the base quality */
    private boolean handleBaseQuality=false;
    /** should we display unclipped parts ?*/
    private boolean useClipped=false;
    private int minMappingQuality=0;
   
    private SamRecordListPacker packer=new PileupSamRecordListPacker();//new SimpleSamRecordListpacker();//
   
    protected VizBam(
            File bamFile,
            IndexedFastaSequenceFile indexedFastaSequenceFile
            )
        {
    	this(new SAMFileReader(bamFile),
    			indexedFastaSequenceFile
    			);
    	this.samFileReaderOwner=true;
        }
   
    
	public void setUseClipped(boolean useClipped)
		{
		this.useClipped = useClipped;
		}
	
	public boolean isUseClipped() {
		return useClipped;
		}

    
    
    protected VizBam(
    		SAMFileReader samFileReader,
            IndexedFastaSequenceFile indexedFastaSequenceFile
            )
        {
        this.samFileReader=samFileReader;
        this.samFileReader.setValidationStringency(ValidationStringency.SILENT);
        this.indexedFastaSequenceFile=indexedFastaSequenceFile;
        if(this.indexedFastaSequenceFile!=null)
	        {
	        this.samSequenceDictionary=indexedFastaSequenceFile.getSequenceDictionary();
	        }
        
        List<SamRecordFilter> L=new ArrayList<SamRecordFilter>();
        L.add(new FailsVendorReadQualityFilter());
        L.add(new DuplicateReadFilter());
        L.add(new NotPrimaryAlignmentFilter());
        this.samRecordFilter=new AggregateFilter(L);
        }
   
    public boolean isHandleBaseQuality()
    	{
		return handleBaseQuality;
		}
   
    public void setHandleBaseQuality(boolean handleBaseQuality)
    	{
		this.handleBaseQuality = handleBaseQuality;
		}
    
    public int getMinMappingQuality()
        {
        return minMappingQuality;
        }   
   
    public void close()
        {
        if(this.samFileReaderOwner) this.samFileReader.close();
        }
   
    protected void cleanup()
        {
        }
    
    /** skip all insertions at begin */
    private int getCigarBeginIndex(final List<CigarElement> cigarElements)
    	{
    	if(isUseClipped()) return 0;
    	int i=0;
    	while(i< cigarElements.size())
    		{
    		CigarElement ce=cigarElements.get(i);
    		if(ce.getOperator()!=CigarOperator.I && ce.getOperator()!=CigarOperator.S)
    			{
    			break;
    			}
    		++i;
    		}
    	
    	return i;
    	}
    
    /** skip all insertions at end */
    private int getCigarEndIndex(final List<CigarElement> cigarElements)
    	{
    	int i=cigarElements.size();
    	if(isUseClipped()) return i;
    	while(i -1 >=0)
    		{
    		CigarElement ce=cigarElements.get(i-1);
    		if(!(ce.getOperator()==CigarOperator.I || ce.getOperator()==CigarOperator.S))
    			{
    			break;
    			}
    		--i;
    		}
    	//return i;
    	return cigarElements.size();
    	}

    
    public SamRecordFilter getSamRecordFilter()
    	{
		return samRecordFilter;
		}
    
    protected abstract void startAlign(final String chromName,final int position);
    protected abstract void endAlign(final String chromName,final int position);
    protected abstract void printReference(final CharSequence reference);
    protected abstract void printRuler(final CharSequence reference);

    protected abstract void startRow();
    protected abstract void endRow();
    protected abstract void startSamRecord(final SAMRecord record);
    protected abstract void endSamRecord(final SAMRecord record);
    protected abstract void startCigar(final SAMRecord record,final CigarElement ce);
    protected abstract void endCigar(final SAMRecord record,final CigarElement ce);
    protected abstract void write(char c);
    protected abstract void startBase(char base,int qual);
    protected abstract void endBase();
    
    
	protected int left(final SAMRecord rec)
		{
		return isUseClipped()?rec.getUnclippedStart():rec.getAlignmentStart();
		}
	
	protected int right(final SAMRecord rec)
		{
		return isUseClipped()?rec.getUnclippedEnd():rec.getAlignmentEnd();
		}
    
    public void align(final String chromName,final int position)
        {
        cleanup();
        startAlign(chromName,position);
        LOG.info("align:"+chromName+":"+position);
        List<SAMRecord> L=new ArrayList<SAMRecord>();
        SAMRecordIterator iter=this.samFileReader.query(chromName, position, position+this.nCols, false);
        while(iter.hasNext())
            {
            SAMRecord record=iter.next();
            if(record.getCigar()==null) continue;//can happen
            if(record.getReadUnmappedFlag()) continue;
            if(getSamRecordFilter().filterOut(record)) continue;
            if(record.getMappingQuality() < getMinMappingQuality() ) continue;
            if(!chromName.equals(record.getReferenceName())) continue;
            L.add(record);
            }
        iter.close();
       
        //find biggest gaps
       
        Map<Integer, Integer> genomicPos2gapSize=new HashMap<Integer, Integer>();
        Map<Integer,Consensus> consensus=new HashMap<Integer,Consensus>();
        for(SAMRecord rec:L)
            {
            int refPos=left(rec);
           
            List<CigarElement> cigarElements=rec.getCigar().getCigarElements();
            for(int i=getCigarBeginIndex(cigarElements);
            		i< getCigarEndIndex(cigarElements) ;
            		++i)
                {
                CigarElement ce=cigarElements.get(i);
                switch(ce.getOperator())
                    {
                    case H : break; // ignore hard clips
                    case P : break; // ignore pads
                    case S : break;
                    case I:
                        {
                        Integer gapSize=genomicPos2gapSize.get(refPos);
                        if(gapSize==null) gapSize=0;
                        if(ce.getLength()> gapSize)
                            {
                            LOG.info("Insertion "+ce.getLength()+" at "+refPos+" "+rec.getCigarString()+"/"+left(rec));
                            genomicPos2gapSize.put(refPos, ce.getLength());
                            }
                        break;
                        }
                    case N : //cont. -- reference skip
                    case D : //cont
                    case EQ:
                    case M:
                    case X:
                        {
                        for(int b=0;b<ce.getLength();++b )
                        	{
                        	consensus.put(refPos, new Consensus());
                        	refPos++;
                        	}
                        break;
                        }
                    default: throw new IllegalStateException("op:"+ce.getOperator());
                    }
                }
            }
        int refPos=position;
        int pixel2refPos[]=new int[this.nCols];
        SAMSequenceRecord ssr=null;
        ReferenceSequence referenceSequence=null;
        if(this.samSequenceDictionary!=null)
        	{
            ssr=this.samSequenceDictionary.getSequence(chromName);
            if(ssr!=null)
            	{
            	referenceSequence=this.indexedFastaSequenceFile.getSubsequenceAt(chromName,position,
            			Math.min(position+this.nCols+1, ssr.getSequenceLength())
            			);
            	}
        	}
        StringBuilder refSequence=new StringBuilder(this.nCols);
       
        /** current pixel_x */
        int pixel_x=0;
        
        for(pixel_x=0;pixel_x< pixel2refPos.length;++pixel_x)
            {
            Integer gapSize=genomicPos2gapSize.get(refPos);
            if(gapSize!=null)
                {
                LOG.info("gap "+gapSize+" at "+refPos);
                while(pixel_x< pixel2refPos.length && gapSize>0)
                    {
                    pixel2refPos[pixel_x++]=-1;
                    gapSize--;
                    refSequence.append("*");
                    }
                }
            if(pixel_x< pixel2refPos.length)
                {
            	char base='N';
            	if(referenceSequence!=null)
            		{
            		int idx=refPos-position;
            		if(referenceSequence!=null &&
            			idx >=0 && 	
            			idx < referenceSequence.getBases().length)
            			{
            			base=(char)referenceSequence.getBases()[idx];
            			}
            		}
                refSequence.append(base);
                pixel2refPos[pixel_x]=refPos;
                ++refPos;
                }
            }
        printReference(refSequence);
     
        /** Ruler */
        StringBuilder ruler=new StringBuilder(this.nCols);
        pixel_x=0;
        refPos=position;
        while(pixel_x< pixel2refPos.length)
            {
            if(pixel2refPos[pixel_x]==-1 || !((pixel2refPos[pixel_x]-position)%10==0))
            	{
            	ruler.append(SPACE1);
            	}
            else
            	{
            	String posStr=String.valueOf(pixel2refPos[pixel_x]);
            	for(int i=0;i< posStr.length() && pixel_x < pixel2refPos.length;++i)
            		{
            		ruler.append(posStr.charAt(i));
            		 ++pixel_x;
            		}
            	}
            
            ++pixel_x;
            }
        printRuler(ruler);
       
        this.packer.setUseClipped(this.isUseClipped());
        List<List<SAMRecord>> rows=packer.pack(L);
        
        
        for(List<SAMRecord> row:rows)
            {
            startRow();
            pixel_x=0;
            refPos=position;
            for(SAMRecord samRecord:row)
                {
                startSamRecord(samRecord);
                byte readBases[]=samRecord.getReadBases();
                byte readQualities[]=samRecord.getBaseQualities();
                /* samRecRefPos!=refpos because the read can start BEFORE 'position'. */
                int samRecRefPos=left(samRecord);
                //reference position for that read.                
                while(refPos< samRecRefPos &&
                	  pixel_x < pixel2refPos.length)
                    {
                	if( pixel2refPos[pixel_x]==-1)
                		{
                		write(SPACE2);
                		pixel_x++;
                		}
                	else
                		{
                		write(SPACE3);
                		refPos++;
                		pixel_x++;
                		}
                	}
                int readPos=0;
                List<CigarElement> cigarElements=samRecord.getCigar().getCigarElements();
             
                //count readPos for ignored left prefix
                
                for(int i=0;
        			//isUseClipped()==false && <- NO need as getCigarBeginIndex returns i==0
        			i< getCigarBeginIndex(cigarElements);++i)
                	{
                	final  CigarElement ce=cigarElements.get(i);
                    switch(ce.getOperator())
                        {
	                    case I:
	                    case S:readPos+=ce.getLength();
	                    	break;
                    	default: throw new IllegalStateException();
                        }
                	}
                for(int i= getCigarBeginIndex(cigarElements);
                		i< getCigarEndIndex(cigarElements) &&
                		pixel_x < pixel2refPos.length ;
                		++i)
                    {
                    final  CigarElement ce=cigarElements.get(i);
                    startCigar(samRecord, ce);
                    switch(ce.getOperator())
                        {
                        case H : break; // ignore hard clips
                        case P : /* refPos+=ce.getLength();*/ break; // ignore pads
                        case S : break;
                        case I:
                            {
                            for(int b=0;b<ce.getLength();++b)
                                {
                                if(refPos>=position )
                                    {
                                	if(isHandleBaseQuality()) startBase(
                                			(char)readBases[readPos],
                                			(byte)(readQualities==null || readPos> readQualities.length ? 0: readQualities[readPos])
                                			);
                                    write((char)readBases[readPos]);
                                    if(isHandleBaseQuality()) endBase();
                                    pixel_x++;
                                    }
                              
                                readPos++;
                                }
                            break;
                            }
                        case N : //cont. -- reference skip
                        case D :
                            {
                            for(int b=0;b<ce.getLength() && pixel_x < pixel2refPos.length ;++b)
                                 {
                                 if(samRecRefPos>=position)
                                     {
                                     while(pixel2refPos[pixel_x]==-1 || pixel2refPos[pixel_x]>samRecRefPos)
                                         {
                                         write('.');
                                         pixel_x++;
                                         }
                                     write('>');
                                     pixel_x++;
                                     refPos++;
                                     }
                                 samRecRefPos++;
                                 }
                            break;
                            }
                        case EQ: case M: case X:
                            { 	
                            	
                            	
                            for(int b=0;b<ce.getLength() && pixel_x < pixel2refPos.length ;++b)
                                {
                            	
                                if(samRecRefPos>=position)
                                    {
                                    while(pixel_x< pixel2refPos.length &&
                                    	(pixel2refPos[pixel_x]==-1 || pixel2refPos[pixel_x]>samRecRefPos))
                                        {
                                        write('*');
                                        pixel_x++;
                                        }
                                    if(pixel_x < pixel2refPos.length)
                                    	{
                                    	if(isHandleBaseQuality()) startBase(
                                    			(char)readBases[readPos],
                                    			(byte)(readQualities==null || readPos> readQualities.length ? 0: readQualities[readPos])
                                    			);
                                    	write((char)readBases[readPos]);
                                    	if(isHandleBaseQuality())  endBase();
                                    	pixel_x++;
                                    	}
                                    refPos++;
                                    }
                                
                                samRecRefPos++;
                                readPos++;
                                }
                            break;
                            }
                        default: throw new IllegalStateException("op:"+ce.getOperator());
                        }
                    endCigar(samRecord, ce);
                   
                    }
               endSamRecord(samRecord);
               /*
                System.err.println("\t"+samRecord.getCigarString()+" "+
                    " align-start: "+samRecord.getAlignmentStart()+
                    " align-end: "+samRecord.getAlignmentEnd()+
                    " "+ getCigarBeginIndex(cigarElements));*/
                }
            endRow();
           
            }
        endAlign(chromName, position);
        }
   
    
    }
