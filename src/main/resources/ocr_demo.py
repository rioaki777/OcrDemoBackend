# coding: utf-8
import sys
import os.path
import cv2
from paddleocr import PaddleOCR
from PIL import Image, ImageEnhance, ImageFont, ImageDraw
import numpy as np
import cv2
from loguru import logger
import json
import re

def run_ocr(img_path):
    img = Image.open(img_path).convert('L')
    np_img = np.asarray(img)
    
    ocr = PaddleOCR(
        use_gpu=False,
        lang = "en",
        det_limit_side_len=img.size[1],
        max_text_length = 20,
        show_log=False
        )
        
    result = ocr.ocr(img=np.asarray(np_img), det=True, rec=True, cls=False)
    if not result or result[0] is None:
        logger.debug(f'not detected.')
        return
    
    #　検知文字列を画像に入力
    output_img = cv2.cvtColor(np_img.copy(), cv2.COLOR_BGR2RGB)
    for detection in result[0]:
        top_left = tuple([int(i) for i in detection[0][0]])
        bottom_right = tuple([int(i) for i in detection[0][2]])
        bottom_left = tuple([int(i) for i in detection[0][3]])
        found_text = detection[1][0]
        
        output_img = cv2.rectangle(output_img, top_left, bottom_right, (0, 255, 0), 1)
        output_img = cv2.putText(output_img, found_text, top_left, cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 0, 0), 1, cv2.LINE_AA)

    # 結果画像保存
    output_img = cv2.cvtColor(output_img, cv2.COLOR_RGB2BGR)
    cv2.imwrite(img_path, output_img)

if __name__ == '__main__':
    # loggerを定義
    logger.add("ocr.log", format="[{time:YYYY.MM.DD HH:mm:ss}] <lvl>{message}</lvl>", level='DEBUG', enqueue=True)
    
    args = sys.argv
    if len(args) <= 1 or not os.path.isfile(args[1]):
        logger.error("Please specify image path.")
        sys.exit()

    img_path = args[1]
    try:
        run_ocr(img_path)
    except Exception as e:
        logger.debug(e)
    
    logger.remove()