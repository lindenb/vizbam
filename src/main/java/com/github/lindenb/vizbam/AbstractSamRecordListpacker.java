package com.github.lindenb.vizbam;

import java.util.Collections;
import java.util.List;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordComparator;
import net.sf.samtools.SAMRecordCoordinateComparator;

public abstract class AbstractSamRecordListpacker implements SamRecordListPacker
	{
	private boolean useClipped=false;
	protected AbstractSamRecordListpacker()
		{
		}
	protected SAMRecordComparator getSAMRecordComparator()
		{
		return new SAMRecordCoordinateComparator();
		}
	protected void sortSamRecords(final List<SAMRecord> records)
		{
		Collections.sort(records, getSAMRecordComparator());
		}
	@Override
	public void setUseClipped(boolean useClipped)
		{
		this.useClipped = useClipped;
		}
	
	@Override
	public boolean isUseClipped() {
		return useClipped;
		}
	
	protected int left(final SAMRecord rec)
		{
		return isUseClipped()?rec.getUnclippedStart():rec.getAlignmentStart();
		}
	
	protected int right(final SAMRecord rec)
		{
		return isUseClipped()?rec.getUnclippedEnd():rec.getAlignmentEnd();
		}

	
	@Override
	public abstract List<List<SAMRecord>> pack(final List<SAMRecord> records);
	
	}
