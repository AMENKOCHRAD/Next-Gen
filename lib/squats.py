import cv2
import numpy as np
import mediapipe as mp
import time
import pyrealsense2 as rs  # Bibliothèque pour la RealSense

# Configuration de MediaPipe pour la détection de pose
mp_pose = mp.solutions.pose
pose = mp_pose.Pose(static_image_mode=False, min_detection_confidence=0.5, min_tracking_confidence=0.5)
mp_drawing = mp.solutions.drawing_utils

# Fonction pour calculer l'angle entre trois points
def calculate_angle(a, b, c):
    a = np.array(a)  # Premier point
    b = np.array(b)  # Point central
    c = np.array(c)  # Dernier point

    radians = np.arctan2(c[1] - b[1], c[0] - b[0]) - np.arctan2(a[1] - b[1], a[0] - b[0])
    angle = np.abs(radians * 180.0 / np.pi)

    if angle > 180.0:
        angle = 360 - angle

    return angle

# Fonction pour vérifier la position des squats
def check_squat(results):
    if results.pose_landmarks:
        landmarks = results.pose_landmarks.landmark

        # Coordonnées des hanches et genoux
        hip = landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y  # Position verticale de la hanche
        knee = landmarks[mp_pose.PoseLandmark.LEFT_KNEE.value].y  # Position verticale du genou

        # Vérification de la position
        if abs(hip - knee) < 0.05:  # Seuil pour genoux et hanches au même niveau
            return "Squatting"
        else:
            return "Standing"

    return "No pose detected"

# Configuration de la caméra RealSense
pipeline = rs.pipeline()
config = rs.config()
config.enable_stream(rs.stream.color, 640, 480, rs.format.bgr8, 30)  # Flux RGB

# Démarrer la caméra RealSense
pipeline.start(config)

# Compteur pour les squats
squat_count = 0

# Variable pour suivre l'état précédent des squats
prev_squat_state = "Standing"

# Délai pour éviter les doubles comptages
last_count_time = time.time()
count_delay = 1  # Délai de 1 seconde entre les comptages

try:
    while True:
        # Attendre pour un ensemble cohérent de frames
        frames = pipeline.wait_for_frames()
        color_frame = frames.get_color_frame()
        if not color_frame:
            continue

        # Convertir l'image en tableau numpy
        color_image = np.asanyarray(color_frame.get_data())

        # Convertir l'image en RGB pour MediaPipe
        image_rgb = cv2.cvtColor(color_image, cv2.COLOR_BGR2RGB)

        # Détection de la pose
        results = pose.process(image_rgb)

        # Dessiner les landmarks de la pose sur l'image
        if results.pose_landmarks:
            mp_drawing.draw_landmarks(
                color_image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS)

        # Vérifier la position des squats
        squat_status = check_squat(results)
        if squat_status == "Squatting" and prev_squat_state == "Standing" and (time.time() - last_count_time) > count_delay:
            squat_count += 1
            last_count_time = time.time()
        prev_squat_state = squat_status

        # Afficher le compteur sur l'image
        cv2.putText(color_image, f"Squats: {squat_count}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2, cv2.LINE_AA)

        # Afficher l'image sans redimensionnement
        cv2.imshow('Squat Monitoring with RealSense', color_image)

        # Quitter si la touche 'q' est pressée
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

finally:
    # Arrêter la caméra RealSense
    pipeline.stop()
    cv2.destroyAllWindows()