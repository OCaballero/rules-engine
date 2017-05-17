package com.oliver.spark_drools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.oliver.spark_drools.utils.DroolsUtils;

public class AppCompiler {

	public static void main(String[] args) throws FileNotFoundException {

		if (args.length != 1 && args.length != 4) {
			System.out.println("Argumentos 1 ó 4");
			System.exit(1);
		}

		if (args.length == 1) {
			DroolsUtils.compileXLS(new FileInputStream(args[0]));
		} else {
			int init = Integer.parseInt(args[2]);
			int finish = Integer.parseInt(args[3]);

			DroolsUtils.compileXLSWithTemplate(new FileInputStream(args[0]), new FileInputStream(args[1]), init,
					finish);
		}

	}
}
