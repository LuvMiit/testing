package org.niias.asrb.kn.myversion;

import com.querydsl.core.Tuple;

import java.util.*;
import java.util.stream.Collectors;

    public class SQLConverter {
        public List<BlanksViewsDTO> getAll(List<Tuple> res1, List<Tuple> res2) {
            List<BlanksViewsDTO> dtoList = new ArrayList<>();
            for (Tuple t : res1) {
                boolean m = false;//id в списке
                for (BlanksViewsDTO oneDTO : dtoList) {//если в списке уже есть пользователь с таким id, то добавить ему год, месяц, array из полученной строки запроса
                    if (oneDTO.getCreatedUserId() == t.get(2, Integer.class)) {
                        String[] separatedInputId = t.get(4, String.class).replaceAll("\\{", "").replaceAll("}", "").split(",");
                        List<String> stringListId = Arrays.asList(separatedInputId);
                        List<Integer> blankIdList = new ArrayList<Integer>(stringListId.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList()));
                        int year = t.get(0, Integer.class);
                        int month = t.get(1, Integer.class);
                        /*Multimap<Integer, Integer> multimap = ArrayListMultimap.create();
                        multimap.put(year, month);
                        oneDTO.getBlankIdList().put(multimap, blankIdList);*/
                        Map<Integer, Integer> map = new HashMap<>();
                        map.put(year, month);
                        oneDTO.getBlankIdList().put(map, blankIdList);
                        m = true;
                    }

                }
                if (!m) {
                    BlanksViewsDTO dto = new BlanksViewsDTO();
                    dto.setCreatedUserId(t.get(2, Integer.class));//установить user_id
                    //dto.setCountBlankId((t.get(3, Number.class)).intValue());

                    //array_agg
                    String[] separatedInputId = t.get(4, String.class).replaceAll("\\{", "").replaceAll("}", "").split(",");
                    List<String> stringListId = Arrays.asList(separatedInputId);
                    List<Integer> blankIdList = new ArrayList<Integer>(stringListId.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList()));

                    //year, month, put array_agg
                    int year = t.get(0, Integer.class);
                    int month = t.get(1, Integer.class);

                    //multimap
                /*Multimap<Integer, Integer> multimap = ArrayListMultimap.create();
                multimap.put(year, month);
                Map<Multimap<Integer, Integer>, List<Integer>> newlist = new HashMap<>();
                newlist.put(multimap, blankIdList);
                dto.setBlankIdList(newlist);*/
                    Map<Integer, Integer> map = new HashMap<>();
                    map.put(year, month);
                    Map<Map<Integer, Integer>, List<Integer>> newlist = new HashMap<>();
                    newlist.put(map, blankIdList);
                    dto.setBlankIdList(newlist);

                    dtoList.add(dto);
                }
            }
            System.out.println(dtoList);


/*for (int i = 1; i <= 12; i++) {
            multimap.put(2021, i);
            if(dtoList.get(0).getBlankIdList().get(multimap) != null)
                System.out.println(dtoList.get(0).getBlankIdList().get(multimap).size());
            else
                System.out.println(0);
        }*/
            for (Tuple t : res2) {
                boolean m = false;//id в списке
                for (BlanksViewsDTO oneDTO : dtoList) {//если в списке уже есть пользователь с таким id, то добавить ему год, месяц, array из полученной строки запроса
                    if (oneDTO.getCreatedUserId() == t.get(2, Integer.class)) {
                        String[] separatedInputId = t.get(4, String.class).replaceAll("\\{", "").replaceAll("}", "").split(",");
                        List<String> stringListViewId = Arrays.asList(separatedInputId);
                        List<Integer> blankViewIdList = new ArrayList<Integer>(stringListViewId.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList()));
                        int year = t.get(0, Integer.class);
                        int month = t.get(1, Integer.class);
                        Map<Integer, Integer> map = new HashMap<>();
                        map.put(year, month);

                        Map<Map<Integer, Integer>, List<Integer>> newlist = new HashMap<>();
                        newlist.put(map, blankViewIdList);
                        if (oneDTO.getBlankViewIdList() == null || oneDTO.getBlankViewIdList().isEmpty())
                            oneDTO.setBlankViewIdList(newlist);
                        else
                            oneDTO.getBlankViewIdList().put(map, blankViewIdList);
                        m = true;
                    }

                }
                if (!m) {
                    BlanksViewsDTO dto = new BlanksViewsDTO();//если пользователь сделал только view
                    dto.setCreatedUserId(t.get(2, Integer.class));//установить user_id
                    //dto.setCountBlankId((t.get(3, Number.class)).intValue());

                    //array_agg
                    String[] separatedInputId = t.get(4, String.class).replaceAll("\\{", "").replaceAll("}", "").split(",");
                    List<String> stringListViewId = Arrays.asList(separatedInputId);
                    List<Integer> blankViewIdList = new ArrayList<Integer>(stringListViewId.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList()));

                    //year, month, put array_agg
                    int year = t.get(0, Integer.class);
                    int month = t.get(1, Integer.class);

                    Map<Integer, Integer> map1 = new HashMap<>();
                    map1.put(year, month);
                    Map<Map<Integer, Integer>, List<Integer>> newlist = new HashMap<>();
                    newlist.put(map1, blankViewIdList);
                    dto.setBlankViewIdList(newlist);

                    dtoList.add(dto);
                }
            }
            return dtoList;
        }
    }


