;; Board
;; --------------
;;
;; We use two 1-dimensional boolean-type arrays to store the moves each player has made on the board.
;;

(ns tictactoe.engine)

(use '[clojure.string :only (split)])

;;
;; Game Interface
;;

(defn map-or [a b] (or a b))
(defn deserialise [block marker] (or (and block marker) nil))

(defn print-board
	"Prints the board in a human-readable format."
	[{:keys [x-blocks o-blocks size] :as board}]
	(let [pretty-board (map map-or
		(map deserialise x-blocks (repeat (* size size) "X"))
		(map deserialise o-blocks (repeat (* size size) "O")))]
		(println "")
		(println "-------------------")
		(println "")
		(doseq [item (partition size pretty-board)]
			(println item)
		)
	)
	board)

(defn prompt [message]
  (println message)
  (read-line))

(defn parse-int
	[s]
	(Integer. (re-find  #"\d+" s)))

(defn get-move
	[{:keys [size] :as board}]
	(let [move (prompt (format "What's your move? [row col] [0-%d 0-%d]" size size))]
		(map parse-int (split move #"\s+"))
	))

(defn get-next-player
	[{:keys [o-blocks x-blocks first-player] :as board}]
	(let [
		o-count (get (frequencies o-blocks) true 0)
		x-count (get (frequencies x-blocks) true 0)]
		(case
			(and (<= o-count x-count) (= first-player "O"))
			true "O"
			false "X"
		)
	))

(defn print-next-player
	[board]
	(println "")
	(println "Next: " (get-next-player board))
	board)

;;
;; Core Logic
;;
(defn new-board
	"Create new and empty board."
	[size first-player]
	{:size size
	 :first-player first-player
	 :x-blocks (vec (replicate (* size size) false))
	 :o-blocks (vec (replicate (* size size) false))
	})

(defn position
	[size row col]
	(+ (* size row) col))

(defn direction-diffs
	[]
	[[0 1] [1 0] [1 1]])

(defn follow-direction
	[position direction size]
	(loop [
		positions [position]
	]
		(if (not (every? (fn [a] (< a size)) (map + (last positions) direction)))
		positions
		(recur (conj positions (map + (last positions) direction)))
	)))

(follow-direction [0 0] [0 1] 3)

(defn generate-directions
	[size]
	(let [
		top-starts (follow-direction [0 0] [0 1] size)
		left-starts (follow-direction [1 0] [1 0] size)
	 ] 
	(filter (fn [x] (= (count x) size))
		(for
			[
				start (into [] (concat top-starts left-starts))
				direction (direction-diffs)
			]
			(follow-direction start direction size)))
	))

(defn call-winner-in-direction
	[blocks size direction player]
	(loop [cnt size dirs direction]
		(if (zero? cnt)
		player
		(and
			(nth blocks (position size (first (first dirs)) (last (first dirs))))
			(recur (dec cnt) (rest dirs))
		)
	)))

(defn call-winner
	[{:keys [o-blocks x-blocks size] :as board}]
	(or
		(first (filter (comp not false?) (map (fn [d] (call-winner-in-direction o-blocks size d "O")) (generate-directions size))))
		(first (filter (comp not false?) (map (fn [d] (call-winner-in-direction x-blocks size d "X")) (generate-directions size))))
	)
)

;;
;; Game Loop
;;

(defn apply-move
	[{:keys [x-blocks o-blocks size] :as board} move]
	(let [next-player (get-next-player board)
		  position (position size (first move) (last move))]
		(assoc board
			:x-blocks (assoc x-blocks position (= next-player "X"))
			:o-blocks (assoc o-blocks position (= next-player "O")))))


(defn player-turn
	"Apply a player's mark to the board."
	[board]
	(-> board print-board print-next-player)
	(let
		[board (-> board (apply-move (get-move board)))]
		(case (call-winner board)
			"X" (println "Crosses have won!")
			"O" (println "Noughts have won!")
			(recur board)
		)
	)
)

; (follow-direction [0 0] [0 1] 3)
; (generate-directions 3)
; (call-winner (apply-move (new-board 3 "O") (get-move (new-board 3 "O"))))
; (player-turn (new-board 3 "O"))

(defn game-loop
  "Full game loop."
  [size first-player]
  (let [board (new-board size first-player)]
  	(player-turn board)
  )
)

; (defn -main [& args]
; 	(let [
; 		size (prompt "What size would you like the board to be? [3-N]")
; 		first-player (prompt "Who would you like to go first? [O or X]")
; 	]
; 	(game-loop size "O"))
; )

(game-loop 3 "O")
