from PIL import Image
import os

folder = "fastlane/metadata/android/en-US/images/phoneScreenshots"

for filename in os.listdir(folder):
    if filename.endswith(".png") or filename.endswith(".jpg"):
        path = os.path.join(folder, filename)
        im = Image.open(path)
        im_resized = im.resize((1080, 1920), Image.LANCZOS)
        im_resized.save(path)
        print(f"Resized {filename} to 1080x1920")
