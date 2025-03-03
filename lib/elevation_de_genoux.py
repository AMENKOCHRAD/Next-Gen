import cv2
import numpy as np
import mediapipe as mp
import time
import pyrealsense2 as rs  # Bibliothèque pour la RealSense

# Configuration de MediaPipe pour la détection de pose
mp_pose = mp.solutions.pose
pose = mp_pose.Pose(static_image_mode=False, min_detection_confidence=0.5, min_tracking_confidence=0.5)
mp_drawing = mp.solutions.drawing_utils

# Fonction pour vérifier les Mountain Climbers
def check_mountain_climber(results):
    if results.pose_landmarks:
        landmarks = results.pose_landmarks.landmark

        # Coordonnées des genoux et des épaules
        left_knee = [landmarks[mp_pose.PoseLandmark.LEFT_KNEE.value].x, landmarks[mp_pose.PoseLandmark.LEFT_KNEE.value].y]
        right_knee = [landmarks[mp_pose.PoseLandmark.RIGHT_KNEE.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_KNEE.value].y]
        left_shoulder = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
        right_shoulder = [landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]

        # Calcul de la distance entre les genoux et les épaules
        left_knee_shoulder_distance = np.linalg.norm(np.array(left_knee) - np.array(left_shoulder))
        right_knee_shoulder_distance = np.linalg.norm(np.array(right_knee) - np.array(right_shoulder))

        # Vérification de la position
        if left_knee_shoulder_distance < 0.15 or right_knee_shoulder_distance < 0.15:  # Seuil pour genou proche de l'épaule
            return "Climbing"
        else:
            return "Neutral"

    return "No pose detected"

# Configuration de la caméra RealSense
pipeline = rs.pipeline()
config = rs.config()
config.enable_stream(rs.stream.color, 640, 480, rs.format.bgr8, 30)  # Flux RGB

# Démarrer la caméra RealSense
pipeline.start(config)

# Compteur pour les Mountain Climbers
climber_count = 0

# Variable pour suivre l'état précédent des Mountain Climbers
prev_climber_state = "Neutral"

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

        # Vérifier la position des Mountain Climbers
        climber_status = check_mountain_climber(results)
        if climber_status == "Climbing" and prev_climber_state == "Neutral" and (time.time() - last_count_time) > count_delay:
            climber_count += 1
            last_count_time = time.time()
        prev_climber_state = climber_status

        # Afficher le compteur sur l'image
        cv2.putText(color_image, f"Mountain Climbers: {climber_count}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2, cv2.LINE_AA)

        # Afficher l'image sans redimensionnement
        cv2.imshow('Mountain Climber Monitoring with RealSense', color_image)

        # Quitter si la touche 'q' est pressée
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

finally:
    # Arrêter la caméra RealSense
    pipeline.stop()
    cv2.destroyAllWindows()