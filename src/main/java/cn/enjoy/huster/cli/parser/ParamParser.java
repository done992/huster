package cn.enjoy.huster.cli.parser;

import java.io.IOException;

public interface ParamParser {
  String[] parse(String[] args, ParamBuilder builder) throws IOException;
}
