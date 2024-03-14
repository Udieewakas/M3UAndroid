package com.m3u.data.parser

import com.m3u.data.parser.internal.EpgData
import java.io.InputStream

interface EpgParser : Parser<InputStream, EpgData>