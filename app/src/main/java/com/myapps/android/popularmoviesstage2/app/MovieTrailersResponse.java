package com.example.android.popularmovies.app;

import java.util.List;


public class MovieTrailersResponse {



    private int id;
    private List<?> quicktime;

    private List<YoutubeEntity> youtube;

    public void setId(int id) {
        this.id = id;
    }

    public void setQuicktime(List<?> quicktime) {
        this.quicktime = quicktime;
    }

    public void setYoutube(List<YoutubeEntity> youtube) {
        this.youtube = youtube;
    }

    public int getId() {
        return id;
    }

    public List<?> getQuicktime() {
        return quicktime;
    }

    public List<YoutubeEntity> getYoutube() {
        return youtube;
    }

    public static class YoutubeEntity {
        private String name;
        private String size;
        private String source;
        private String type;

        public void setName(String name) {
            this.name = name;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public String getSource() {
            return source;
        }

        public String getType() {
            return type;
        }
    }
}
