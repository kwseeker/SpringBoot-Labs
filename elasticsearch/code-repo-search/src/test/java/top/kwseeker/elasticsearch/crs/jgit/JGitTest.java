package top.kwseeker.elasticsearch.crs.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JGitTest {

    private String localRepoPath;

    @BeforeTest
    public void init() {
        localRepoPath = "/home/lee/mywork/github/learningEs";
    }

    @Test
    public void connectRepo() {
        //克隆远程仓库
        //String remotePath = "https://github.com/user/repo.git";
        //Git.cloneRepository()
        //        .setURI(remotePath)
        //        .setDirectory(new File("/path/to/clone/repo"))
        //        .call();

        // 本地仓库
        try (Git git = Git.open(new File(localRepoPath))) {
            Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
                System.out.println(commit.getName() + " " + commit.getFullMessage());
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDiffFileList() {
        try (Git git = Git.open(new File(localRepoPath))) {
            // 获取master分支最近一次提交
            Iterable<RevCommit> commits = git.log()
                    .add(git.getRepository().resolve("refs/heads/master"))
                    .setMaxCount(1).call();
            RevCommit latestCommit = commits.iterator().next();

            String commitId = "e0f0b84375d565a2f0b09463e254e0c4052b3829";
            RevCommit lastIndexedCommit = git.getRepository().parseCommit(ObjectId.fromString(commitId));

            // 两次提交文件差异分析
            ObjectReader reader = git.getRepository().newObjectReader();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, latestCommit.getTree());
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, lastIndexedCommit.getTree());
            List<DiffEntry> entries = git.diff().
                    setNewTree(newTreeIter).
                    setOldTree(oldTreeIter).
                    call();
            // 获取变更文件列表
            List<String> changedFiles = new ArrayList<>();
            for (DiffEntry entry : entries) {
                if (!entry.getNewPath().equals("/dev/null")) {
                    changedFiles.add(entry.getNewPath());
                }
            }

            System.out.println("变更文件列表:");
            for (String f : changedFiles) {
                System.out.println(f);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }
}
