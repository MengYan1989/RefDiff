package refdiff.evaluation.icse;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import refdiff.evaluation.EvaluationUtils;
import refdiff.evaluation.RefactoringSet;
import refdiff.evaluation.RefactoringType;
import refdiff.evaluation.ResultComparator;

public class RunIcseEval2 {
	
	private EnumSet<RefactoringType> refactoringTypes = EnumSet.complementOf(EnumSet.of(RefactoringType.PULL_UP_ATTRIBUTE, RefactoringType.PUSH_DOWN_ATTRIBUTE, RefactoringType.MOVE_ATTRIBUTE));
	private EvaluationUtils evalUtils;
	
	public RunIcseEval2(String tempFolder) {
		evalUtils = new EvaluationUtils(tempFolder);
	}

	public static void main(String[] args) throws Exception {
		new RunIcseEval2(args.length > 0 ? args[0] : "D:/tmp/").run();
	}
	
	public void run() throws Exception {
		IcseDataset data = new IcseDataset();
		List<RefactoringSet> expected = data.getExpected();
		
		ResultComparator rc = new ResultComparator();
		rc.dontExpect(data.getNotExpected());
		
		Set<String> whitelist = new HashSet<>(Arrays.asList(
			"abbf32571232db81a5343db17a933a9ce6923b44",
			"18a7bd1fd1a83b3b8d1b245e32f78c0b4443b7a7",
			"446e2537895c15b404a74107069a12f3fc404b15",
			"d3533c1a0716ca114d294b3ea183504c9725698f",
			"04bcfe98dbe7b05e508559930c21379ece845732",
			"364f50274d4b4b83d40930c0d2c4d0e57fb34589"
			));
		
		for (RefactoringSet rs : expected) {
			String project = rs.getProject();
			String commit = rs.getRevision();
			if (!whitelist.contains(commit)) continue;
			try {
				evalUtils.prepareSourceCode(project, commit);
			} catch (RuntimeException e) {
				System.out.println(String.format("Skipped %s %s", project, commit));
				System.err.println(e.getMessage());
				continue;
			}
			rc.expect(rs);
			rc.compareWith("RefDiff", evalUtils.runRefDiff(project, commit));
		}
		
		rc.printSummary(System.out, refactoringTypes);
		rc.printDetails(System.out, refactoringTypes);
	}
	
}
