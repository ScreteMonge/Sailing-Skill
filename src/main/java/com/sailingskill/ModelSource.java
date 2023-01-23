package com.sailingskill;

public enum ModelSource
{
    sail0("https://github.com/ScreteMonge/Waters/raw/main/sail0_vertex_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sail0_face_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sail0_color_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sail0_transparency_export.csv"),
    sailP45("https://github.com/ScreteMonge/Waters/raw/main/sailP45_vertex_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP45_face_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP45_color_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP45_transparency_export.csv"),
    sailP90("https://github.com/ScreteMonge/Waters/raw/main/sailP90_vertex_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP90_face_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP90_color_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailP90_transparency_export.csv"),
    sailN45("https://github.com/ScreteMonge/Waters/raw/main/sailN45_vertex_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN45_face_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN45_color_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN45_transparency_export.csv"),
    sailN90("https://github.com/ScreteMonge/Waters/raw/main/sailN90_vertex_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN90_face_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN90_color_export.csv",
            "https://github.com/ScreteMonge/Waters/raw/main/sailN90_transparency_export.csv"),
    ;

    public final String vertexLink;
    public final String faceLink;
    public final String colourLink;
    public final String transparencyLink;

    ModelSource(String vertexLink, String faceLink, String colourLink, String transparencyLink)
    {
        this.vertexLink = vertexLink;
        this.faceLink = faceLink;
        this.colourLink = colourLink;
        this.transparencyLink = transparencyLink;
    }
}
