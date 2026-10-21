import os
from pyannote.audio import Pipeline

audio_file = r"..\whisper-old\clean_telugu.wav"

token = os.environ.get("HF_TOKEN")

if not token:
    raise RuntimeError("HF_TOKEN environment variable is not set.")

print("Loading speaker diarization model...")

pipeline = Pipeline.from_pretrained(
    "pyannote/speaker-diarization-community-1",
    token=token
)

print("Model loaded.")
print("Starting diarization...")

output = pipeline(audio_file)

print("\n===== SPEAKER DIARIZATION =====\n")

for turn, speaker in output.speaker_diarization:
    print(
        f"{turn.start:8.2f}s --> "
        f"{turn.end:8.2f}s   {speaker}"
    )