# tictactoe

Tic-tac-toe implemented in Clojure in the CLI.

## Usage

- Checkout the repo
- Install [lein](https://leiningen.org/#install)
- In the project root run `lein trampoline run`

## Implemented so far

- NxN board support
- Can select to start as O or as X
- Draw

## Not implemented

- Any non-happy path handling whatsoever, e.g. throw failure using pre/post conditions when field to mark is already marked or a while loop in the prompt when user enters bad input
- Computer player

## What needs testing

- (print-board (new-board 3 "O"))
- (call-winner-in-direction `[one block with sufficient potential]` `[some board sizes]` `[some directions]` `[the two players]`)
- (follow-direction `[some starting points]` `[some directions]` `[with some different board sizes]`)
- (generate-directions `[with some different board sizes]`)
- (apply-move (new-board 3 `[with both players as first players]`) `[with some different moves]`)
- (call-winner `[with a number of different boards]`)
- (player-turn `[with a number of different boards]`)

## License

Apache 2.0. See LICENSE for details
