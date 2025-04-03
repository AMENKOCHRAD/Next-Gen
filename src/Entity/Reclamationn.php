<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use Doctrine\Common\Collections\Collection;
use App\Entity\Traitementreclamationn;

#[ORM\Entity]
class Reclamationn
{

    #[ORM\Id]
    #[ORM\Column(type: "integer")]
    private int $id;

    #[ORM\Column(type: "string", length: 255)]
    private string $sujet;

    #[ORM\Column(type: "string", length: 255)]
    private string $description;

    #[ORM\Column(type: "datetime")]
    private \DateTimeInterface $date;

    #[ORM\Column(type: "string")]
    private string $categorie;

    #[ORM\Column(type: "text")]
    private string $pieces_jointes;

    #[ORM\Column(type: "integer")]
    private int $user;

    #[ORM\Column(type: "string", length: 250)]
    private string $statut;

    #[ORM\Column(type: "string", length: 255)]
    private string $email;

    public function getId()
    {
        return $this->id;
    }

    public function setId($value)
    {
        $this->id = $value;
    }

    public function getSujet()
    {
        return $this->sujet;
    }

    public function setSujet($value)
    {
        $this->sujet = $value;
    }

    public function getDescription()
    {
        return $this->description;
    }

    public function setDescription($value)
    {
        $this->description = $value;
    }

    public function getDate()
    {
        return $this->date;
    }

    public function setDate($value)
    {
        $this->date = $value;
    }

    public function getCategorie()
    {
        return $this->categorie;
    }

    public function setCategorie($value)
    {
        $this->categorie = $value;
    }

    public function getPieces_jointes()
    {
        return $this->pieces_jointes;
    }

    public function setPieces_jointes($value)
    {
        $this->pieces_jointes = $value;
    }

    public function getUser()
    {
        return $this->user;
    }

    public function setUser($value)
    {
        $this->user = $value;
    }

    public function getStatut()
    {
        return $this->statut;
    }

    public function setStatut($value)
    {
        $this->statut = $value;
    }

    public function getEmail()
    {
        return $this->email;
    }

    public function setEmail($value)
    {
        $this->email = $value;
    }

    #[ORM\OneToMany(mappedBy: "reclamation_id", targetEntity: Traitementreclamationn::class)]
    private Collection $traitementreclamationns;

        public function getTraitementreclamationns(): Collection
        {
            return $this->traitementreclamationns;
        }
    
        public function addTraitementreclamationn(Traitementreclamationn $traitementreclamationn): self
        {
            if (!$this->traitementreclamationns->contains($traitementreclamationn)) {
                $this->traitementreclamationns[] = $traitementreclamationn;
                $traitementreclamationn->setReclamation_id($this);
            }
    
            return $this;
        }
    
        public function removeTraitementreclamationn(Traitementreclamationn $traitementreclamationn): self
        {
            if ($this->traitementreclamationns->removeElement($traitementreclamationn)) {
                // set the owning side to null (unless already changed)
                if ($traitementreclamationn->getReclamation_id() === $this) {
                    $traitementreclamationn->setReclamation_id(null);
                }
            }
    
            return $this;
        }
}
