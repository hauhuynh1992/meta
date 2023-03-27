package com.aimenext.metawater.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArMarker {
    /**
     * 検出したARマーカーの座標
     */
    //private String markerRect;

    /**
     * 検出したARマーカーのID
     */
    private int markerId;

    /**
     * キャプチャ画像の保存先ディレクトリ
     */
    private String dir;

    /**
     * キャプチャ画像のファイル名
     */
    private String file;

    /**
     * 検出したARマーカーの座標を取得します。
     *
     * @return 検出したARマーカーの座標
     */
//    public String getMarkerRect() {
//        return markerRect;
//    }

    /**
     * 検出したARマーカーの座標を設定します。
     *
     * @param markerRect 検出したARマーカーの座標
     */
//    public void setMarkerRect(String markerRect) {
//        this.markerRect = markerRect;
//    }

    /**
     * 検出したARマーカーのIDを取得します。
     *
     * @return 検出したARマーカーのID
     */
    public int getMarkerId() {
        return markerId;
    }

    /**
     * 検出したARマーカーのIDを設定します。
     *
     * @param markerId 検出したARマーカーのID
     */
    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }

    /**
     * キャプチャ画像の保存先ディレクトリを取得します。
     *
     * @return キャプチャ画像の保存先ディレクトリ
     */
    public String getDir() {
        return dir;
    }

    /**
     * キャプチャ画像の保存先ディレクトリを設定します。
     *
     * @param dir キャプチャ画像の保存先ディレクトリ
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * キャプチャ画像のファイル名を取得します。
     *
     * @return キャプチャ画像のファイル名
     */
    public String getFile() {
        return file;
    }

    /**
     * キャプチャ画像のファイル名を設定します。
     *
     * @param file キャプチャ画像のファイル名
     */
    public void setFile(String file) {
        this.file = file;
    }

}

