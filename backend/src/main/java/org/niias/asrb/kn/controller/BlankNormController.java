package org.niias.asrb.kn.controller;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.BlankNormRepository;
import org.niias.asrb.kn.repository.BlankRepository;
import org.niias.asrb.kn.repository.FileRepository;
import org.niias.asrb.kn.service.FileService;
import org.niias.asrb.kn.util.CompletionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.niias.asrb.kn.model.QBlankFile.blankFile;
import static org.niias.asrb.kn.model.QBlankNorm.blankNorm;

@RestController
public class BlankNormController {

    @Inject
    private BlankNormRepository normRepo;

    @Inject
    private BlankRepository blankRepo;

    @Inject
    private FileRepository fileRepo;

    @Inject
    private JPAQueryFactory qf;

    @Inject
    private FileService fileService;

    @Value("${filePath}")
    private String filePath;


    private boolean isValid(Integer month, Period period, Integer plan){
        if (month == null)
            return false;
        if (month < 1 || month > 12)
            return false;
        if (period == Period.selected && !CompletionUtil.isMonthSet(plan, month))
            return false;
        return true;
    }

    @RequestMapping("/api/blank/norm/file")
    public List<BlankFile> listNormFile(Integer normId, Integer month){
        BlankNorm norm = normRepo.findById(normId).orElseThrow(IllegalStateException::new);
        int setMonth = norm.getSetMonthOfPeriod(month);
        List<BlankFile> list = qf.from(blankNorm)
            .select(blankFile)
            .join(blankNorm.files, blankFile)
            .where(blankFile.month.eq(setMonth), blankNorm.id.eq(normId))
            .fetch();
        return filterUniq(list);
    }

    private List<BlankFile> filterUniq(List<BlankFile> list) {
        List<String> added = new ArrayList<>();
        List<BlankFile> uniq = new ArrayList<>();
        for (BlankFile file : list) {
            String key = file.getFileRef().getFileName()+file.getFileRef().getFileType();
            if (!added.contains(key)) {
                uniq.add(file);
                added.add(key);
            }
        }
        return uniq;
    }

    static class DocNormParam{
        public Integer blankId;
        public Integer normId;
        public Integer documentId;
        public List<Integer> month;
        public String name;
    }

    @RequestMapping(value = "/api/blank/norm", method = RequestMethod.DELETE)
    public ResponseEntity rmDocumentNorm(@RequestParam Integer blankId, @RequestParam Integer normId) {
        Blank blank = blankRepo.findById(blankId).get();
        Iterator<BlankNorm> it = blank.getNorms().iterator();
        while (it.hasNext()){
            BlankNorm norm = it.next();
            if (norm.getId().equals(normId) && norm instanceof BlankNormDocument)
            {
                norm.getFiles().forEach(f-> fileRepo.deleteById(f.getFileRef().getFileId()));
                it.remove();
            }
        }
        blankRepo.save(blank);
        return ResponseEntity.ok().build();
    }

        @RequestMapping(value = "/api/blank/norm", method = RequestMethod.POST)
    public ResponseEntity addDocumentNorm(@RequestBody DocNormParam param)
    {

        BlankNormDocument docNorm = param.normId == null ? new BlankNormDocument() : (BlankNormDocument) normRepo.findById(param.normId).get();
        docNorm.setDocId(param.documentId);
        docNorm.setName(param.name);
        docNorm.setPlan(new CompletionMark());
        param.month.stream().forEach(i-> docNorm.getPlan().setMonth(i));

        if (param.normId == null)
        {
            Blank blank = blankRepo.findById(param.blankId).get();
            blank.getNorms().add(docNorm);
            blankRepo.save(blank);
        }else{
            Iterator<BlankFile> it = docNorm.getFiles().iterator();
            while (it.hasNext())
            {
                BlankFile bf = it.next();
                if (!param.month.contains(bf.getMonth()))
                {
                    fileRepo.deleteById(bf.getFileRef().getFileId());
                    it.remove();
                }
            }
            normRepo.save(docNorm);
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/api/blank/norm/upload-file", method = RequestMethod.POST)
    public List<BlankFile> uploadFileToNorm(@RequestParam Integer month, @RequestParam Integer normId, @RequestParam MultipartFile[] files){
        BlankNorm norm = normRepo.findById(normId).orElseThrow(IllegalStateException::new);
        Integer plan =  BlankNormDocument.class.isAssignableFrom(norm.getClass()) ? ((BlankNormDocument) norm).getPlan().getValue() : norm.getCustomPeriod().getValue();

        if (!isValid(month, norm.getPeriod(), plan))
            throw new IllegalStateException();

        List<BlankFile> fileList = Arrays.stream(files).map(it-> {
            File file = new File();
                    file.setData("file moved".getBytes());
                    file.setMimeType(it.getContentType());
            file.setName(it.getOriginalFilename());
            return file;
        })
        .map(fileRepo::save)
        .map(file->new FileRef(file.getId(), file.getName(), file.getMimeType()))
        .map(ref-> {
            BlankFile bf = new BlankFile();
            bf.setMonth(norm.getSetMonthOfPeriod(month) == 0 ? month : norm.getSetMonthOfPeriod(month));
            bf.setFileRef(ref);
            return bf;
        })
        .collect(Collectors.toList());
        fileList.forEach(file -> {
            MultipartFile multipartFile = Arrays.stream(files).filter(rawFile ->
                    rawFile.getOriginalFilename() != null && rawFile.getOriginalFilename().equals(file.getFileRef().getFileName())).findFirst().orElse(null);
            if (multipartFile != null) {
                try {
                    fileService.writeFile(file.getFileRef().getFileId().toString(), multipartFile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if (norm.getSetMonthOfPeriod(month) == 0)
            norm.getCompletion().setMonth(month);

        norm.getFiles().addAll(fileList);
        normRepo.save(norm);
        return norm.getFiles().stream().filter(it-> it.getMonth() == month).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/blank/norm/rm-file", method = RequestMethod.POST)
    public List<BlankFile> rmFile(@RequestParam Integer month, @RequestParam Integer normId, @RequestParam Integer fileId){
        BlankNorm norm = normRepo.findById(normId).orElseThrow(IllegalStateException::new);
        Iterator<BlankFile> iter = norm.getFiles().iterator();
        while (iter.hasNext())
        {
            BlankFile bf = iter.next();
            if (bf.getId().equals(fileId)) {
                fileRepo.deleteById(bf.getFileRef().getFileId());
                iter.remove();
                boolean isFilledMonth = norm.getFiles().stream().anyMatch(file -> Objects.equals(file.getMonth(), month));
                if (!isFilledMonth) {
                    norm.getCompletion().unsetMonth(month);
                }
            }
        }

        norm = normRepo.save(norm);
        return norm.getFiles().stream().filter(it-> it.getMonth() == month).collect(Collectors.toList());
    }

}
