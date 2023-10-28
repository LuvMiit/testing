package org.niias.asrb.kn.controller;



import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.Document;
import org.niias.asrb.kn.model.File;
import org.niias.asrb.kn.repository.DocumentRepository;
import org.niias.asrb.kn.repository.FileRepository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.niias.asrb.kn.model.QDocument.document;


@RestController
@RequestMapping
public class DoucmentController {


    @Resource
    private FileRepository fileRepo;

    @Resource
    private DocumentRepository documentRepository;

    @Resource
    private JPAQueryFactory qf;



    @GetMapping("api/documents")
    public Iterable<Document> getDocuments() {
     ArrayList<Document> allDocs = (ArrayList<Document>) documentRepository.findAll();
     allDocs.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        return allDocs;
    }

    @PostMapping("api/documents/change-active")
    public Optional<Document> changeIsActive(@RequestBody Map obj) {
        int id = (int) obj.get("fileId");
        boolean isActive = (boolean) obj.get("isActive");
        Optional<Document> docToChange = documentRepository.findById(id);
        Document nd =  docToChange.get();
        nd.setActive(isActive);
        documentRepository.save(nd);
        return docToChange;
    }


    @PostMapping("api/documents/save-files")
    public String saveFiles(@RequestBody Map filename) throws IOException {

        String fn = String.valueOf(filename.get("fileName"));
        Document doc = new Document();
        doc.setName(fn);

        documentRepository.save(doc);

        return "saved";
    }



    @RequestMapping(value = "api/documents/upload-file/{docId}", method = RequestMethod.GET)
    @ResponseBody
    public File getDocument(@PathVariable int docId) {
        Optional<File> found = fileRepo.findById(docId);
        File ff = found.get();

      return ff;

    }


    public static class DocTo {

        public DocTo(Document doc){
            this.id = doc.getId();
            this.name = doc.getName();
        }

        public Integer id;
        public String name;
    }

    @RequestMapping("/api/documents/blank-api")
    public List<DocTo> listActualForBlank(){
        return ((List<Document>) qf.from(document)
                .where(document.active.isTrue())
                .orderBy(document.id.desc())
                .fetch())
                .stream()
                .map(DocTo::new)
                .collect(Collectors.toList());
    }




}
