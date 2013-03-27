package com.github.lindenb.vizbam;

import java.util.List;

import net.sf.samtools.SAMRecord;

public interface SamRecordListPacker
	{
	public List<List<SAMRecord>> pack(final List<SAMRecord> records);
	}
